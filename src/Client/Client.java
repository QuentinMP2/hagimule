package Client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

import Diary.Annuaire;

public class Client extends Thread {

    public static boolean etat = true;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Erreur arguments ligne de commande : java Client <ClientID> <Url Diary>");
        } else {
            Random randGen = new Random();
            int port = randGen.nextInt(40000, 64000);
            String addrClient = args[0] + ":" + port;
            String url = args[1] + ":4000/diary";

            Thread downloaderThread = new Thread(() -> {
                System.out.println("Downloader Thread running.");
                new DownloaderImpl(addrClient, url);
            });


            try {
                Annuaire annuaire = (Annuaire) Naming.lookup(url);

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
                throw new RuntimeException("Annuaire pas trouvé (lancé ?)");
            } catch (MalformedURLException e) {
                throw new RuntimeException("adresse malformée");
            } catch (IOException e) {
                System.out.print("");
            }
        }
    }

}
