import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private final ContentManager datamartManager = new ContentManager();
    private final DatabaseManager databaseController = new DatabaseManager();

    public Controller() throws SQLException {
    }

    private void Task() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    for (Weather weather : datamartManager.findMaxTemperatures()) {
                        databaseController.insertMaxTemperature(weather);
                    }
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    for (Weather weather : datamartManager.findMinTemperatures()) {
                        databaseController.insertMinTemperature(weather);
                    }
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.schedule(timerTask, 5000, 3600000);
    }

    public void run() {
        Task();
    }
}
