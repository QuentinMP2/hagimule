package Common;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ListClientImpl implements ListClient {

    private ArrayList<Integer> listeClient;

    public ListClientImpl(ArrayList<Integer> listC){
        this.listeClient = listC;
    }

    public ArrayList<Integer> getClients() throws RemoteException {
        return listeClient;
    }
}
