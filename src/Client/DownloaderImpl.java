package Client;

import Common.FichierImpl;
import Common.Requete;
import Common.RequeteImpl;
import Diary.Annuaire;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class DownloaderImpl implements Downloader {

    /** Identifiant du client. */
    private int clientID;

    /** URL de l'annuaire. */
    private String url;

    public DownloaderImpl(int clientID, String url) {
        this.clientID = clientID;
        this.url = url;
        runDownloader();
    }


    public void getHelp(){
        System.out.println("Commandes possibles : \n" +
                "   help\n" +
                "   ls\n" +
                "   dl <filename>\n" +
                "   add <filename>");
    }

    public void getFile(String filename, Annuaire annuaire) throws IOException, InterruptedException {
        String[] lc = annuaire.getClients(filename).getClients().split(",");
        for (String j : lc) {
            System.out.println("recup sur le client : " +j);
            int i = Integer.parseInt(j);
            Socket s = new Socket("127.0.0.1", 8080);


            InputStream input = s.getInputStream();
            ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());


            Requete r = new RequeteImpl(filename, lc.length, i, clientID);
            output.writeObject(r);
            String[] filenameL = filename.split("\\.");
            String newname = filenameL[0] + "(" + i + ")." + filenameL[1];
            sleep(3000);
            FileOutputStream outputfile = new FileOutputStream("Output/"+newname);
            byte[] boeuf = new byte[1000000];
            int sizeread = 0;
            while (sizeread != -1) {
                sizeread = input.read(boeuf);
                System.out.println("sizeread : " + sizeread);
                if (sizeread != -1){
                    outputfile.write(boeuf, 0, sizeread);
                }
            }
            s.close();
        }
    }


    private void runDownloader() {
        try (Scanner scanner = new Scanner(System.in)) {
            Annuaire annuaire = (Annuaire) Naming.lookup(url);
            getHelp();
            while (true) {
                System.out.print("> ");
                String[] line = scanner.nextLine().split(" ");
                if (Objects.equals(line[0], "help")) {
                    getHelp();
                }
                if (Objects.equals(line[0], "ls")) {
                    System.out.println(annuaire.listAllFile());
                }
                if (Objects.equals(line[0], "dl")) {
                    if (line.length == 2) {
                        if (annuaire.exist(new FichierImpl(line[1]))) {
                            getFile(line[1], annuaire);
                        } else {
                            System.out.println("fichier non trouvé");
                        }
                    } else {
                        System.out.println("pas de fichier en argument");
                        getHelp();
                    }
                }
            }

        } catch (NotBoundException e) {
            throw new RuntimeException("erreur adresse annuaire introuvable");
        } catch (MalformedURLException e) {
            throw new RuntimeException("erreur url");
        } catch (RemoteException e) {
            throw new RuntimeException(e + " erreur remote");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
