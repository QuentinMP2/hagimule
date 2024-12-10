package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ListClient extends Remote {
    
    public ArrayList<Integer> getClients() throws RemoteException;

}