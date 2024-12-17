package Diary;

import Common.Fichier;
import Common.ListClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Annuaire extends Remote {
    
    boolean ajouter(Fichier file, int client) throws RemoteException;

    ListClient getClients(String fileName) throws RemoteException;

    String listAllFile() throws RemoteException;

    Boolean exist(Fichier file) throws RemoteException;
}
