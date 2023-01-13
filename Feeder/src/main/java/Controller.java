import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    private final Area granCanaria = new Area(28.4, 27.5, -15, -16);
    private final FileManager fileManager = new AemetFileManager();
    private final WeatherSensor weatherSensor = new AemetWeatherSensor();

    private void executeTask() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    fileManager.createDatalakeDirectory();
                    fileManager.createFiles(weatherSensor.readAreaWeather(granCanaria));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        timer.schedule(timerTask, 0, 3600000);
    }

    public void run() {
        executeTask();
    }
}
