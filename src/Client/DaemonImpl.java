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

    /** Identifiant du client. */
    private int clientID;

    /** URL de l'annuaire. */
    private String url;

    /** Socket du client. */
    private Socket client;

    /** Construit un Daemon.
     * @param clientID identifiant du client
     * @param url url de l'annuaire
     * @param socket socket du client
     */
    public DaemonImpl(int clientID, String url, Socket socket) {
        this.clientID = clientID;
        this.url = url;
        this.client = socket;
    }

    public void run() {
        try {
            ObjectInputStream cis = new ObjectInputStream(client.getInputStream());
            /* recup le nom de fichier, la proportion, le numero de la partie, et l'id du client auquel envoyer*/
            Requete r = (RequeteImpl)cis.readObject();

            /* recup le fichier */
            FileInputStream fileInputStream = new FileInputStream("Input/"+r.getFileName());
            long fileSize = Files.size(Paths.get("Input/" +r.getFileName()));

            /* envoyer le fichier */
            OutputStream cos = client.getOutputStream();
            long toSkip = (long) ((r.getPartie()-1)*floor((double) fileSize /r.getDecoupe()));
            /* vérifier qu'on a bien skip la bonne taille */
            long sizeSkip = 0;
            while (sizeSkip < toSkip) {
                sizeSkip += fileInputStream.skip(toSkip - sizeSkip);
            }
            System.out.println("filesize : " + fileSize + ", to skip : " + toSkip + ", to read : " + floor((double) fileSize /r.getDecoupe()));
            int sizeRead = 0;
            int currentRead;
            byte[] boeuf = new byte[(int) fileSize];
            while ((sizeRead < floor((double) fileSize /r.getDecoupe())) && (sizeRead+toSkip < fileSize)) {
                currentRead = fileInputStream.read(boeuf);
                sizeRead += currentRead;
                System.out.println(sizeRead + " Current : " + currentRead);
                cos.write(boeuf, 0, currentRead);
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

