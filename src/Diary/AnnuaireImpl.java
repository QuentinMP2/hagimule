package Diary;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnuaireImpl implements Annuaire {

    HashMap<String, ArrayList<String>> data;

    public AnnuaireImpl() {
        this.data = new HashMap<>();
    }

    public boolean ajouter(Fichier file, String client) {
        return false;
    }

    public ListClient getClients(String fileName) {
        return null;
    }
}
