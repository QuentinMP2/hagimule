package Common;

import java.io.Serializable;

public interface Requete extends Serializable {
    String getFileName();

    int getOffSet();

    int getSize();

    String getClientDemandeur();
}
