package Diary;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import Common.Fichier;
import Common.ListClient;
import Common.ListClientImpl;

public class AnnuaireImpl extends UnicastRemoteObject implements Annuaire {

    HashMap<String, ArrayList<String>> data;
    HashMap<String, Long> complData;
    ArrayList<String> connected;

    public AnnuaireImpl() throws RemoteException {
        super();
        this.data = new HashMap<>();
        this.complData = new HashMap<>();
        this.connected = new ArrayList<>();
    }

    @Override
    public boolean ajouter(Fichier file, String client) throws RemoteException {
        if (!connected.contains(client)) {
            System.out.println("Nouveau client : " + client);
            connected.add(client);
        }
        if (data.containsKey(file.getNom())) {
            System.out.println("Nouveau client sur " + file.getNom());
            ArrayList<String> listC = data.get(file.getNom());
            if (listC.contains(client)) {
                System.out.println("Client deja existant");
            } else {
                listC.add(client);
            }
            data.put(file.getNom(), listC);
            return false;
        } else {
            System.out.println("Nouvelle cle " + file.getNom());
            ArrayList<String> listC = new ArrayList<>();
            listC.add(client);
            data.put(file.getNom(), listC);
            System.out.println("Fichier de taille " + file.getSize());
            complData.put(file.getNom(), file.getSize());
            return true;
        }
    }

    @Override
    public void supprimer(Fichier file, String client) throws RemoteException {

        ArrayList<String> listC = data.get(file.getNom());
        listC.remove(client);
        if (listC.isEmpty()) {
            System.out.println("Le fichier " + file.getNom() + " n'est plus référencé");
            data.remove(file.getNom());
            complData.remove(file.getNom());
        } else {
            System.out.println("Client " + client + " parti sur le fichier " + file.getNom());
            data.put(file.getNom(), listC);
        }

    }


    @Override
    public ListClient getClients(String fileName) throws RemoteException {
        return new ListClientImpl(data.get(fileName));
    }

    @Override
    public long getSize(String filename) throws RemoteException {
        return complData.get(filename);
    }

    @Override
    public String listAllFile() throws RemoteException {
        StringBuilder s = new StringBuilder();
        for (String str: data.keySet()) {
            s.append(str).append(":").append(complData.get(str)).append(" - ");
        }
        return String.valueOf(s);
    }

    @Override
    public Boolean exist(String fileName) throws RemoteException {
        return data.containsKey(fileName);
    }

    @Override
    public void clientLeave(String clientIP) throws RemoteException {
        connected.remove(clientIP);
        System.out.println("Client : " + clientIP + " est parti");
        ArrayList<String> toRemove = new ArrayList<>();
        for (String filename : data.keySet()) {
            ArrayList<String> listC = data.get(filename);
            listC.remove(clientIP);
            if (listC.isEmpty()) {
                System.out.println("Le fichier " + filename + " n'est plus référencé");
                toRemove.add(filename);
            } else {
                data.put(filename, listC);
            }
        }
        for (String filename : toRemove) {
            data.remove(filename);
            complData.remove(filename);
        }
    }

    @Override
    public String _list_conected() throws RemoteException {
        StringBuilder res = new StringBuilder();
        for (String  s : connected) {
            res.append((Objects.equals(connected.getLast(), s)) ? s : s + ",");
        }
        return res.toString();
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        try {
            if (args.length != 1) {
                System.out.println("Erreur nombre d'argument il manque l'adresse de l'annuaire"); 
            } else {
                LocateRegistry.createRegistry(4000);
                Naming.bind(args[0] + ":4000/diary", new AnnuaireImpl());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Mauvaise adresse annuaire");
        }
    }
}
