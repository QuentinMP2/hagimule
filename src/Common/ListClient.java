package Common;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ListClient extends Serializable {
    
    public String getClients() throws RemoteException;

}