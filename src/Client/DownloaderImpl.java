package Client;

import Common.FichierImpl;
import Common.Requete;
import Common.RequeteImpl;
import Diary.Annuaire;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Math.floor;


public class DownloaderImpl implements Downloader {

    /** L'annuaire. */
    private Annuaire annuaire;

    /** Le port de l'annuaire*/
    private String port;

    public DownloaderImpl(Annuaire annuaire, String port) {
        this.annuaire = annuaire;
        this.port = port;
        runDownloader();
    }

    public void getFile(String filename, Annuaire annuaire) throws IOException {

        /**
         * Classe implémentant les threads de téléchargement
         */
        class DownloadThread extends Thread {
            /**
             * Nom du fichier a télécharger
             */
            private String fileName;

            /**
             * IP du client à qui demander le fichier
             */
            private String clientIP;

            /**
             * Taille du fichier cible
             */
            private long fileSize;

            /**
             * Numéro de téléchargement à réaliser
             */
            private int num;
            
            /**
             * Nombre de téléchargements total à réaliser
             */
            private int nbDL;

            /**
             * Constructeur de DownloaderThread
             *
             * @param fileName
             * @param clientIP
             * @param fileSize
             * @param num
             * @param nbDL
             */
            public DownloadThread(String fileName, String clientIP, long fileSize, int num, int nbDL) {
                this.fileName = fileName;
                this.clientIP = clientIP;
                this.fileSize = fileSize;
                this.num = num;
                this.nbDL = nbDL;
            }

            @Override
            public void run() {
                String[] clientInfo = clientIP.split(":");
                String ip = clientInfo[0];
                int port = Integer.parseInt(clientInfo[1]);
                try {
                    // On se connecte au daemon cible
                    Socket s = new Socket(ip, port);

                    // Stream entrant et sortant
                    InputStream input = s.getInputStream();
                    ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());

                    // Nombre de bytes à ignorer dans le fichier
                    long toSkip = (long) ((num - 1) * floor((double) fileSize / (double) nbDL));
                    // Taille totale du fragment à télécharger
                    long size = (num == nbDL) ? (fileSize - toSkip) : (int) floor((double) fileSize / (double) nbDL);
                    //System.out.println("to skip : " + toSkip + ", to read : " + size + "/" + fileSize + ", " + num + "/" + nbDL);

                    // Requête à envoyer au daemon
                    Requete r = new RequeteImpl(fileName, toSkip, size);
                    // Envoi de la requête
                    output.writeObject(r);
                    // Nom variant selon le numéro de fragment du fichier
                    String newName = fileName + "(" + num + ")";
                    // Création du fichier pour enregistrer le fragment
                    FileOutputStream outputFile = new FileOutputStream("Output/" + ((nbDL == 1) ? filename : newName));
                    // Buffer pour récupérer le fichier
                    byte[] boeuf = new byte[(int) (Integer.MAX_VALUE * 0.0001)];
                    int sizeread = 0;
                    while (sizeread != -1) {
                        sizeread = input.read(boeuf, 0, (int) (Integer.MAX_VALUE * 0.0001));
                        if (sizeread != -1) {
                            outputFile.write(boeuf, 0, sizeread);
                        }
                    }

                    // On ferme la connection
                    s.close();
                    // On ferme le fichier d'écriture du fragment
                    outputFile.close();

                } catch (IOException e) {
                    throw new RuntimeException("Erreur socket client : " + clientIP + "\n" + e);
                }
            }
        }

        long t1 = System.currentTimeMillis();
        // Liste des clients qui possèdent le fichier cible
        String req = annuaire.getClients(filename).getClients();
        String[] lc = req.split(",");
        System.out.println("Clients trouvés : " + lc.length + " -> " + Arrays.toString(lc));

        // Récupération la taille du fichier
        long fileSize = annuaire.getSize(filename);
        long t2 = System.currentTimeMillis();
        // Création une liste de thread de la bonne taille
        DownloadThread[] listeThreads = new DownloadThread[lc.length];

        // Lancement les threads de téléchargements
        for (int i = 1; i <= lc.length; i++) {
            // Création des différents threads
            listeThreads[i - 1] = new DownloadThread(filename, lc[i - 1], fileSize, i, lc.length);
            // Lancement des threads
            listeThreads[i - 1].start();
        }

        // Attente de tous les threads avant de continuer
        for (int i = 0; i < lc.length; i++) {
            try {
                listeThreads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Erreur download on thread " + i + "/" + lc.length + " \n" + e);
            }
        }
        long t3 = System.currentTimeMillis();

        /* reconstruction du fichier
         * → nécessaire seulement si on a plus de 1 téléchargement parallèle*/
        if (lc.length != 1) {
            // Fichier final
            FileOutputStream fichierFinal = new FileOutputStream("Output/" + filename);
            for (int i = 1; i <= lc.length; i++) {
                // Récupération du fragment i
                FileInputStream fileInputStream = new FileInputStream("Output/" + filename + "(" + i + ")");
                long size = Files.size(Paths.get("Output/" + filename + "(" + i + ")"));

                long sizeRead = 0;
                int currentRead;
                // On vérifie que la taille du fichier n'est pas trop grosse
                if (size > Integer.MAX_VALUE * 0.001) {
                    byte[] boeuf = new byte[(int) (Integer.MAX_VALUE * 0.0001)];
                    /* Envoyer le fichier */
                    while (sizeRead < size) {
                        currentRead = fileInputStream.read(boeuf, 0, (int) (Integer.MAX_VALUE * 0.0001));
                        sizeRead += currentRead;
                        // Écriture du fragment i dans le fichier final
                        fichierFinal.write(boeuf, 0, currentRead);
                    }
                } else {
                    int smallerSize = (int) ((size > 2000) ? size / 10 : size);
                    byte[] boeuf = new byte[smallerSize];
                    while (sizeRead < size) {
                        currentRead = fileInputStream.read(boeuf, 0, smallerSize);
                        sizeRead += currentRead;
                        // Écriture du fragment i dans le fichier final
                        fichierFinal.write(boeuf, 0, currentRead);
                    }
                }
                fileInputStream.close();
            }
            fichierFinal.close();
        }
        long t4 = System.currentTimeMillis();
        System.out.println("Temps requête info fichier : " + (t2 - t1) +
                "\nTemps récupération des différents fragments : " + (t3 - t2) +
                "\nTemps reconstruction du fichier cible : " + (t4 - t3) +
                "\nTemps total : " + (t4 - t1));
    }

    public void getHelp(){
        System.out.println("Commandes possibles : \n" +
                "   help\n" +
                "   ls\n" +
                "   ls -l (pour afficher les fichier en local)\n" +
                "   ref\n" +
                "   dl <filename>\n" +
                "   add <filename>\n" +
                "   rm <filename>\n" +
                "   size <filename>\n" +
                "   to leave : ctrl + c");
    }

    private void runDownloader() {
        ArrayList<String> fichierUploaded = new ArrayList<>();
        File directoryInput;
        try (Scanner scanner = new Scanner(System.in)) {

            getHelp();
            while (Client.state) {
                System.out.print("> ");
                String[] line = scanner.nextLine().split(" ");
                if (Objects.equals(line[0], "help")) {
                    getHelp();
                } else if (Objects.equals(line[0], "ls")) {
                    if (line.length == 2) {
                        if (Objects.equals(line[1], "-l")) {
                            directoryInput = new File("Input");
                            File[] files = directoryInput.listFiles();
                            System.out.println(Arrays.toString(files).replace("Input/", "").replace("[", "").replace("]", ""));
                        } else {
                            System.out.println("Mauvais argument");
                        }
                    } else {
                        System.out.println(annuaire.listAllFile());
                    }
                } else if (Objects.equals(line[0], "add")){
                    if (line.length == 2) {
                        boolean existe = false;
                        directoryInput = new File("Input");
                        File[] files = directoryInput.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                if (Objects.equals(line[1], f.getName())) {
                                    if (fichierUploaded.contains(line[1])) {
                                        System.out.println(line[1] + " a déjà été enregistré");
                                    } else {
                                        annuaire.ajouter(new FichierImpl(f.getName(), Files.size(Paths.get("Input/" + f.getName()))), port);
                                        fichierUploaded.add(f.getName());
                                    }
                                    existe = true;
                                }
                            }
                            if (!existe) {
                                System.out.println(line[1] + " n'existe pas");
                                System.out.println(Arrays.toString(files).replace("Input/", "").replace("[", "").replace("]", ""));
                            } else {
                                System.out.println("fin ajout : " + line[1]);
                            }
                        } else {
                            System.out.println("erreur pas de fichier a ajouter au diary");
                        }
                    } else {
                        System.out.println("pas de fichier en argument");
                        getHelp();
                    }
                } else if (Objects.equals(line[0], "rm")) {
                    if (line.length == 2) {
                        if (fichierUploaded.contains(line[1])) {
                            annuaire.supprimer(new FichierImpl(line[1]), port);
                            fichierUploaded.remove(line[1]);
                            System.out.println(fichierUploaded);
                        } else {
                            System.out.println("fichier non uploadé");
                        }
                    } else {
                        System.out.println("pas de fichier en argument");
                        getHelp();
                    }
                } else if (Objects.equals(line[0], "ref")) {
                    System.out.println(fichierUploaded);
                } else if (Objects.equals(line[0], "dl")) {
                    if (line.length == 2) {
                        if (annuaire.exist(line[1])) {
                            getFile(line[1], annuaire);
                        } else {
                            System.out.println("fichier non trouvé");
                        }
                    } else {
                        System.out.println("pas de fichier en argument");
                        getHelp();
                    }
                } else if (Objects.equals(line[0], "__getC")){
                    System.out.println(annuaire._list_conected());
                } else {
                    System.out.println("Mauvaise commande : " + line[0]);
                    getHelp();
                }
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("erreur url");
        } catch (RemoteException e) {
            throw new RuntimeException("erreur remote \n" + e);
        } catch (IOException e) {
            throw new RuntimeException("IO exception downloader \n" + e);
        }
    }
}
