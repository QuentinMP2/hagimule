package Common;

import java.io.Serializable;

public interface Requete extends Serializable {
    String getFileName();

    long getOffSet();

    long getSize();

    String getClientDemandeur();
}
