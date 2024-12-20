package Client;

import Common.Requete;
import Common.RequeteImpl;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Math.floor;

public class DaemonImpl extends Thread implements Daemon {

    /** Identifiant du client. */
    private String clientIP;

    /** URL de l'annuaire. */
    private String url;

    /** Socket du client. */
    private Socket client;

    /** Construit un Daemon.
     * @param clientIP identifiant du client
     * @param url url de l'annuaire
     * @param socket socket du client
     */
    public DaemonImpl(String clientIP, String url, Socket socket) {
        this.clientIP = clientIP;
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
            long fileSize = (long)Files.size(Paths.get("Input/" +r.getFileName()));

            /* envoyer le fichier */
            OutputStream cos = client.getOutputStream();

            /* vérifier qu'on a bien skip la bonne taille */
            long sizeSkip = 0;
            while (sizeSkip < r.getOffSet()) {
                sizeSkip += fileInputStream.skip(r.getOffSet() - sizeSkip);
            }

            int sizeRead = 0;
            int currentRead;
            byte[] boeuf = new byte[r.getSize()];
            /* Envoyer le fichier */
            while ((sizeRead < r.getSize())) {
                currentRead = fileInputStream.read(boeuf);
                sizeRead += currentRead;
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

