package Common;

public class RequeteImpl implements Requete {
    private String fileName;
    private int offSet;
    private int size;
    private String clientDemandeur;

    public RequeteImpl(String fileName, int decoupe, int partie, String clientDemandeur) {
        this.fileName = fileName;
        this.offSet = decoupe;
        this.size = partie;
        this.clientDemandeur = clientDemandeur;
    }

    public String getFileName() {
        return fileName;
    }

    public int getOffSet() {
        return offSet;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String getClientDemandeur() {
        return clientDemandeur;
    }
}
