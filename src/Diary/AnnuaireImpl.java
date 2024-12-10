package Diary;

import Common.Fichier;
import Common.ListClient;
import Common.ListClientImpl;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;

public class AnnuaireImpl implements Annuaire {

    HashMap<String, ArrayList<Integer>> data;

    public AnnuaireImpl() {
        this.data = new HashMap<>();
    }

    public boolean ajouter(Fichier file, int client) {
        if (data.containsKey(file.getNom())) {
            ArrayList<Integer> listC = data.get(file.getNom());
            listC.add(client);
            data.put(file.getNom(), listC);
            return false;
        } else {
            ArrayList<Integer> listC = new ArrayList<>();
            listC.add(client);
            data.put(file.getNom(), listC);
            return true;
        }
    }

    public ListClient getClients(String fileName) {
        return new ListClientImpl(data.get(fileName));
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        try {
            LocateRegistry.createRegistry(4000);
            Naming.bind(args[0] + "4000", new AnnuaireImpl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Mauvaise adresse annuaire");
        }
    }
}
