import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    private final DatamartManager datamartManager = new DatamartManager();
    private final DatabaseController databaseController = new DatabaseController();

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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    for (Weather weather : datamartManager.findMinTemperatures()) {
                        databaseController.insertMinTemperature(weather);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.schedule(timerTask, 5000, 3600000);
    }

    public void run() throws IOException, SQLException {
        Task();
    }
}
