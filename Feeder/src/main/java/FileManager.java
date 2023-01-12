import java.io.IOException;
import java.util.List;

public interface FileManager {
    void createDatalakeDirectory();

    void createFiles(List<Weather> weatherList) throws IOException;
}
