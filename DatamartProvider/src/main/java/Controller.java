import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private final ContentManager contentManager = new ContentManager();
    private final DatabaseManager databaseController = new DatabaseManager();

    public Controller() throws SQLException {
    }

    private void executeTask() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    for (Weather weather : contentManager.findMaxTemperatures()) {
                        databaseController.insertMaxTemperature(weather);
                    }
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    for (Weather weather : contentManager.findMinTemperatures()) {
                        databaseController.insertMinTemperature(weather);
                    }
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.schedule(timerTask, 5000, 86400000);
    }

    public void run() {
        executeTask();
    }
}
