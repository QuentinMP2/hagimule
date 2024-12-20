package Common;

public class FichierImpl implements Fichier {

    private final String nom;

    private final int size;

    public FichierImpl(String nom, int size) {
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

    public int getSize() {
        return size;
    }
}
