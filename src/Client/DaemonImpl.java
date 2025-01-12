package Client;

import Common.Requete;
import Common.RequeteImpl;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DaemonImpl extends Thread implements Daemon {

    /** Socket du client. */
    private Socket client;

    /** Construit un Daemon.
     * @param socket socket du client
     */
    public DaemonImpl(Socket socket) {
        this.client = socket;
    }

    public void run() {
        try {
            ObjectInputStream cis = new ObjectInputStream(client.getInputStream());
            /* récup le nom de fichier, la proportion, le numéro de la partie, et l'id du client auquel envoyer*/
            Requete r = (RequeteImpl)cis.readObject();

            /* récup le fichier */
            FileInputStream fileInputStream = new FileInputStream("Input/"+r.getFileName());
            long fileSize = (long)Files.size(Paths.get("Input/" +r.getFileName()));

            /* envoyer le fichier */
            OutputStream cos = client.getOutputStream();

            /* vérifier qu'on a bien skip la bonne taille */
            long sizeSkip = 0;
            while (sizeSkip < r.getOffSet()) {
                sizeSkip += fileInputStream.skip(r.getOffSet() - sizeSkip);
            }

            long sizeRead = 0;
            int currentRead;
            // On vérifie que la taille du fichier n'est pas trop grosse
            if (r.getSize() > Integer.MAX_VALUE*0.001) {
                int smallerSize = (int)(Integer.MAX_VALUE*0.0001);
                byte[] boeuf = new byte[smallerSize];
                /* Envoyer le fichier */
                while (sizeRead < r.getSize()) {
                    currentRead = fileInputStream.read(boeuf, 0, (int)((sizeRead+smallerSize > r.getSize()) ? r.getSize() - sizeRead : smallerSize));
                    sizeRead += currentRead;
                    cos.write(boeuf, 0, currentRead);
                }

            } else {
                int smallerSize = (int)((r.getSize() > 2000) ? r.getSize()/10 : r.getSize());
                byte[] boeuf = new byte[smallerSize];
                /* Envoyer le fichier */
                while(sizeRead < r.getSize()) {
                    currentRead = fileInputStream.read(boeuf, 0, (int)((sizeRead + smallerSize > r.getSize()) ? r.getSize() - sizeRead : smallerSize));
                    sizeRead += currentRead;
                    cos.write(boeuf, 0, currentRead);
                }
            }

            fileInputStream.close();
            client.close();

        } catch (IOException e) {
            System.out.println("Daemon run IOException");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Mauvaise réception de la requête");
        }
    }
}

