package Diary;

import Common.Fichier;
import Common.ListClient;
import Common.ListClientImpl;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class AnnuaireImpl extends UnicastRemoteObject implements Annuaire {

    HashMap<String, ArrayList<Integer>> data;

    public AnnuaireImpl() throws RemoteException {
        super();
        this.data = new HashMap<>();
    }

    @Override
    public boolean ajouter(Fichier file, int client) throws RemoteException {
        System.out.println("ajout");
        if (data.containsKey(file.getNom())) {
            System.out.println("fichier " + file.getNom() + " deja existant :" + client);
            ArrayList<Integer> listC = data.get(file.getNom());
            listC.add(client);
            data.put(file.getNom(), listC);
            return false;
        } else {
            System.out.println("Nouvelle cle " + file.getNom());
            ArrayList<Integer> listC = new ArrayList<>();
            listC.add(client);
            data.put(file.getNom(), listC);
            return true;
        }
    }

    @Override
    public ListClient getClients(String fileName) throws RemoteException{
        return new ListClientImpl(data.get(fileName));
    }

    @Override
    public String listAllFile() throws RemoteException {
        StringBuilder s = new StringBuilder();
        for (String str: data.keySet()) {
            s.append(str).append(" - ");
        }
        return String.valueOf(s);
    }

    @Override
    public Boolean exist(Fichier file) throws RemoteException {
        return data.containsKey(file.getNom());
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        try {
            LocateRegistry.createRegistry(4000);
            Naming.bind("//localhost:4000" + "/diary", new AnnuaireImpl());
            //Naming.bind(args[0] + "4000", new AnnuaireImpl());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Mauvaise adresse annuaire");
        }
    }
}
