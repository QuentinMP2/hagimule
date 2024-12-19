package Client;

import Common.FichierImpl;
import Diary.Annuaire;

import javax.lang.model.element.NestingKind;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;

public class Client extends Thread {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Erreur arguments ligne de commande : java Client <ClientID> <Url Diary>");
        } else {
            int clientID = Integer.parseInt(args[0]);
            String url = args[1];

            Thread downloaderThread = new Thread(() -> {
                System.out.println("Downloader Thread running.");
                new DownloaderImpl(clientID, url);
            });

            try {
                ArrayList<String> fichierDispo = new ArrayList<>();
                Annuaire annuaire = (Annuaire) Naming.lookup(url);
                File directory = new File("Input");
                System.out.println("Input directory :" + directory.getAbsolutePath());
                File[] files = directory.listFiles();
                if (files != null) {
                    for(File f : files) {
                        annuaire.ajouter(new FichierImpl(f.getName()), clientID);
                        fichierDispo.add(f.getName());
                    }
                } else {
                    System.out.println("erreur pas de fichier a ajouter au diary");
                }

                System.out.println("fin ajout : " + fichierDispo);
                ServerSocket ss = new ServerSocket(8080);

                // Lancement du Downloader
                downloaderThread.start();

                while (true) {
                    new DaemonImpl(clientID, url, ss.accept()).start();
                }
            } catch (IOException e) {
                System.out.println("Daemon IOException \n");
            } catch (NotBoundException e) {
                throw new RuntimeException("Mauvaise adresse annuaire");
            }
        }
    }

}
