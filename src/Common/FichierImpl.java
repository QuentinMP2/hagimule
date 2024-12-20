package Common;

public class FichierImpl implements Fichier {

    private final String nom;

    private final long size;

    public FichierImpl(String nom, long size) {
        this.nom = nom;
        this.size = size;
    }

    public FichierImpl(String nom) {
        this.nom = nom;
        this.size = -1;
    }

    public String getNom() {
        return nom;
    }

    public long getSize() {
        return size;
    }
}
