import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class AemetWeatherSensor implements WeatherSensor {
    private final String url = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas";

    public List<Weather> readAreaWeather(Area area) {
        APIkey apIkey = new APIkey();
        String response = apIkey.request(url);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        String data = jsonObject.get("datos").getAsString();
        String content = apIkey.request(data);
        JsonArray jsonElements = gson.fromJson(content, JsonArray.class);
        return getAreaWeather(area, jsonElements);
    }

    private List<Weather> getAreaWeather(Area area, JsonArray jsonElements) {
        return jsonElements.asList().stream()
                .filter(this::hasTemperature)
                .filter(jsonElement -> area.latmin() < jsonElement.getAsJsonObject().get("lat").getAsDouble() &&
                        jsonElement.getAsJsonObject().get("lat").getAsDouble() < area.latmax())
                .filter(jsonElement -> area.lonmin() < jsonElement.getAsJsonObject().get("lon").getAsDouble() &&
                        jsonElement.getAsJsonObject().get("lon").getAsDouble() < area.lonmax())
                .map(this::toWeather)
                .collect(Collectors.toList());
    }

    private boolean hasTemperature(JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().get("ta") != null;
    }

    private Weather toWeather(JsonElement jsonElement) {
        String id = jsonElement.getAsJsonObject().get("idema").getAsString();
        String ubi = jsonElement.getAsJsonObject().get("ubi").getAsString();
        String date = jsonElement.getAsJsonObject().get("fint").getAsString();
        double ta = jsonElement.getAsJsonObject().get("ta").getAsDouble();
        double tempmax = jsonElement.getAsJsonObject().get("tamax").getAsDouble();
        double tempmin = jsonElement.getAsJsonObject().get("tamin").getAsDouble();
        return new Weather(id, ubi, date, ta, tempmax, tempmin);
    }
}