package Client;

import Common.FichierImpl;
import Diary.Annuaire;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread {

    public static boolean etat = true;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Erreur arguments ligne de commande : java Client <ClientID> <Url Diary>");
        } else {
            Random randGen = new Random();
            int port = randGen.nextInt(40000, 64000);
            String addrClient = args[0] + ":" + port;
            String url = args[1];

            Thread downloaderThread = new Thread(() -> {
                System.out.println("Downloader Thread running.");
                new DownloaderImpl(addrClient, url);
            });


            try {
                ArrayList<String> fichierDispo = new ArrayList<>();
                Annuaire annuaire = (Annuaire) Naming.lookup(url);
                File directory = new File("Input");
                System.out.println("Input directory :" + directory.getAbsolutePath());
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        annuaire.ajouter(new FichierImpl(f.getName(),(int)Files.size(Paths.get("Input/" +f.getName()))), addrClient);
                        fichierDispo.add(f.getName());
                    }
                } else {
                    System.out.println("erreur pas de fichier a ajouter au diary");
                }

                System.out.println("fin ajout : " + fichierDispo);
                ServerSocket ss = new ServerSocket(port);

                //Prévenir le diary que le client se déconnecte
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            ss.close();
                            etat = false;
                            annuaire.clientLeave(addrClient);
                        } catch (IOException e) {
                            throw new RuntimeException("Erreur stop daemon");
                        }
                    }
                });

                // Lancement du Downloader
                downloaderThread.start();

                while (etat) {
                    new DaemonImpl(addrClient, url, ss.accept()).start();
                }
            } catch (NotBoundException e) {
                throw new RuntimeException("Mauvaise adresse annuaire");
            } catch (RemoteException e) {
                throw new RuntimeException("Erreur remote");
            } catch (MalformedURLException e) {
                throw new RuntimeException("adresse malformée");
            } catch (IOException e) {
                System.out.print("");
            }
        }
    }

}
