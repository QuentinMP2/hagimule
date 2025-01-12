package Diary;

import Common.Fichier;
import Common.ListClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Annuaire extends Remote {

    void ajouter(Fichier file, String port) throws RemoteException;

    void supprimer(Fichier file, String port) throws RemoteException;

    ListClient getClients(String fileName) throws RemoteException;

    long getSize(String filename) throws RemoteException;

    String listAllFile() throws RemoteException;

    Boolean exist(String fileName) throws RemoteException;

    String _getIP(String port) throws RemoteException;

    void clientLeave(String port) throws RemoteException;

    String _list_conected() throws RemoteException;
}
