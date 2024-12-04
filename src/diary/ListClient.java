package Diary;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ListClient extends Remote {
    
    public String getClients() throws RemoteException;

}