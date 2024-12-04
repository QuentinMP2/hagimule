package Diary;

public interface Annuaire {
    
    boolean ajouter(Diary.Fichier file, String client);

    Diary.ListClient getClients(String fileName);
}
