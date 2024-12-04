package Client;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DaemonImpl extends Thread{
    private static int clientID;
    private Socket client;

    public DaemonImpl(Socket s) {
        this.client = s;
    }

    public static void main(String[] args) {
        try {
            clientID = Integer.parseInt(args[0]);
            ArrayList<String> fichierDispo = new ArrayList<>(args.length - 1);
            for (int i = 1; i < args.length - 1; i++) {
                fichierDispo.set(i - 1, args[i]);
            }

            /*donner les fichiers dispos a l annuaire*/

            ServerSocket ss = new ServerSocket(8080 + clientID);
            while (true) {
                new DaemonImpl(ss.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Daemon IOException \n");
        }
    }

    public void run() {
        try {
            InputStream cis = client.getInputStream();

            /* recup le nom de fichier, la proportion, le numero de la partie, et le nom du client auquel envoyer*/

            /* recup le fichier */

            /* envoyer le fichier */

        } catch (IOException e) {
            System.out.println("Daemon run IOException");
        }
    }
}

