package diary;

public interface Annuaire {
    
    boolean ajouter(Fichier file);

    ListClient getClients(String flieName);
}
