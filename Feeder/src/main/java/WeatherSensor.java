import java.util.List;

public interface WeatherSensor {
    List<Weather> readAreaWeather(Area area);
}
