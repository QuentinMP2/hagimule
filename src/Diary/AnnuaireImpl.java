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
    public void ajouter(Fichier file, String port) throws RemoteException {
        String client = "vide";
        try {
            client = getClientHost() + ":" + port;
        } catch (Exception e) {
            System.out.println("erreur getClientHost");
        }
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
        } else {
            System.out.println("Nouvelle cle " + file.getNom() + " de taille " + file.getSize());
            ArrayList<String> listC = new ArrayList<>();
            listC.add(client);
            data.put(file.getNom(), listC);
            complData.put(file.getNom(), file.getSize());
        }
    }

    @Override
    public void supprimer(Fichier file, String port) throws RemoteException {
        String client = "vide";
        try {
            client = getClientHost()+":"+port;
        } catch (Exception e) {
            System.out.println("erreur getclienthost");
        }
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
        boolean estRef = false;
        for(ArrayList<String> lc : data.values()) {
            if (lc.contains(client)) {
                estRef = true;
                break;
            }
        }
        if (!estRef) {
            this.connected.remove(client);
            System.out.println("Le client " + client + " ne référence plus de fichier");
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
        try {
            System.out.println(getClientHost());
        } catch (Exception e) {
            System.out.print("Nope");
        }
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
    public void clientLeave(String port) throws RemoteException {
        String clientIP = "vide";
        try {
            clientIP = getClientHost()+":"+ port;
        } catch (Exception e) {
            System.out.println("erreur getClientHost");
        }
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
    public String _getIP(String port) throws RemoteException {
        try {
            return getClientHost() + ":" + port;
        } catch (Exception e) {
            System.out.println("erreur getClientHost");
            return null;
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
                Naming.bind("//" + args[0] + ":4000/diary", new AnnuaireImpl());
                System.out.println("Annuaire écoute sur le port 4000");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Mauvaise adresse annuaire");
        }
    }
}
