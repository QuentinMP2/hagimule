package Common;

public class RequeteImpl implements Requete {
    private String fileName;
    private int decoupe;
    private int partie;
    private int clientDemandeur;

    private RequeteImpl(String fileName, int decoupe, int partie, int clientDemandeur) {
        this.fileName = fileName;
        this.decoupe = decoupe;
        this.partie = partie;
    }

    public String getFileName() {
        return fileName;
    }

    public int getDecoupe() {
        return decoupe;
    }

    public int getPartie() {
        return partie;
    }

    @Override
    public int getClientDemandeur() {
        return clientDemandeur;
    }
}
