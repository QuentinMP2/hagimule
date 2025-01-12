package Common;

public class RequeteImpl implements Requete {
    private final String fileName;
    private final long offSet;
    private final long size;
    private String clientDemandeur;

    public RequeteImpl(String fileName, long offSet, long size, String clientDemandeur) {
        this.fileName = fileName;
        this.offSet = offSet;
        this.size = size;
        this.clientDemandeur = clientDemandeur;
    }

    public RequeteImpl(String fileName, long offSet, long size) {
        this.fileName = fileName;
        this.offSet = offSet;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public long getOffSet() {
        return offSet;
    }

    public long getSize() {
        return size;
    }

    public String getClientDemandeur() {
        return clientDemandeur;
    }
}
