package Common;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ListClientImpl implements ListClient {

    private String listeClient;

    public ListClientImpl(ArrayList<Integer> listC){
        listeClient = "";
        for (int i : listC) {
            listeClient += (listC.getLast() == i)? i : i + ",";
        }
    }

    public String getClients() throws RemoteException {
        return listeClient;
    }
}
