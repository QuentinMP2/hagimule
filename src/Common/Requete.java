package Common;

import java.io.Serializable;

public interface Requete extends Serializable {
    String getFileName();

    int getDecoupe();

    int getPartie();

    int getClientDemandeur();
}
