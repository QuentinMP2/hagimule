package Common;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;

public class ListClientImpl implements ListClient {

    private final String lc;

    public ListClientImpl(ArrayList<String> listC) {
        StringBuilder listeClient;

        listeClient = new StringBuilder();
        for (String i : listC) {
            listeClient.append((Objects.equals(listC.getLast(), i)) ? i : i + ",");
        }
        this.lc = String.valueOf(listeClient);
    }

    public String getClients() {
        return lc;
    }
}
