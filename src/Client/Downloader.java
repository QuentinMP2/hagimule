package Client;
import java.io.IOException;
import Diary.Annuaire;

public interface Downloader {
    void getFile(String filename, Annuaire annuaire) throws IOException, InterruptedException;

    void getHelp();
}
