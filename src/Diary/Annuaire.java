package Diary;

import Common.Fichier;
import Common.ListClient;

import java.rmi.Remote;

public interface Annuaire extends Remote {
    
    boolean ajouter(Fichier file, int client);

    ListClient getClients(String fileName);
}
