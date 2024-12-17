package Client;

import Common.FichierImpl;
import Diary.Annuaire;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class DownloaderImpl {
    private static int clientID;

    public static void getHelp(){
        System.out.println("Commandes possibles : \n" +
                "   help\n" +
                "   ls\n" +
                "   dl <filename>\n" +
                "   add <filename>");
    }

    public static void getFile(String filename) {
        
    }


    public static void main(String[] args) {
        clientID = Integer.parseInt(args[0]);
        Scanner scanner = new Scanner(System.in);
        try {
            Annuaire annuaire = (Annuaire) Naming.lookup(args[1]);
            getHelp();
            while(true) {
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
                            getFile(line[1]);
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
            throw new RuntimeException("erreur remote");
        } finally {
            scanner.close();
        }
    }
}
