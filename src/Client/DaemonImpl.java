package Client;

import Common.FichierImpl;
import Common.Requete;
import Common.RequeteImpl;
import Diary.Annuaire;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;

import static java.lang.Math.floor;

public class DaemonImpl extends Thread implements Daemon {
    private static int clientID;
    private Socket client;

    public DaemonImpl(Socket s) {
        this.client = s;
    }

    public static void main(String[] args) {
        try {
            clientID = Integer.parseInt(args[0]);
            ArrayList<String> fichierDispo = new ArrayList<>();
            Annuaire annuaire = (Annuaire) Naming.lookup(args[1]);
            File directory = new File("Input");
            System.out.println("Input directory :" + directory.getAbsolutePath());
            File[] files = directory.listFiles();
            if (files != null) {
                for(File f : files) {
                    annuaire.ajouter(new FichierImpl(f.getName()), Integer.parseInt(args[0]));
                    fichierDispo.add(f.getName());
                }
            } else {
                System.out.println("erreur pas de fichier a ajouter au diary");
            }

            System.out.println("fin ajout : " + fichierDispo);
            ServerSocket ss = new ServerSocket(8080);
            while (true) {
                new DaemonImpl(ss.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Daemon IOException \n");
        } catch (NotBoundException e) {
            throw new RuntimeException("Mauvaise adresse annuaire");
        }
    }

    public void run() {
        try {
            ObjectInputStream cis = new ObjectInputStream(client.getInputStream());
            /* recup le nom de fichier, la proportion, le numero de la partie, et l'id du client auquel envoyer*/
            Requete r = (RequeteImpl)cis.readObject();

            /* recup le fichier */
            FileInputStream fileInputStream = new FileInputStream("files/"+r.getFileName());
            long fileSize = Files.size(Paths.get("files/" +r.getFileName()));

            /* envoyer le fichier */
            OutputStream cos = new ByteArrayOutputStream();
            long toSkip = (long) ((r.getPartie()-1)*floor((double) fileSize /r.getDecoupe()));
            /* vérifier qu'on a bien skip la bonne taille */
            long sizeSkip = 0;
            while (sizeSkip < toSkip) {
                sizeSkip += fileInputStream.skip(toSkip - sizeSkip);
            }

            int sizeRead = 0;
            byte[] boeuf = new byte[1024];
            while ((sizeRead < floor((double) fileSize /r.getDecoupe())) && (sizeRead+toSkip < fileSize)) {
                sizeRead += fileInputStream.read(boeuf);
                cos.write(boeuf);
            }

            fileInputStream.close();
            client.close();


        } catch (IOException e) {
            System.out.println("Daemon run IOException");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Mauvaise reception de la requete");
        }
    }
}

