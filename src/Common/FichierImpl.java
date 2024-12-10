package Common;

public class FichierImpl implements Fichier {

    private final String nom;


    public FichierImpl(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

}
