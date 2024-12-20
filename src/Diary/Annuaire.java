package Diary;

import Common.Fichier;
import Common.ListClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Annuaire extends Remote {

    boolean ajouter(Fichier file, String client) throws RemoteException;

    ListClient getClients(String fileName) throws RemoteException;

    int getSize(String filename) throws RemoteException;

    String listAllFile() throws RemoteException;

    Boolean exist(Fichier file) throws RemoteException;

    void clientLeave(String clientIP) throws RemoteException;

    String _list_conected() throws RemoteException;
}
