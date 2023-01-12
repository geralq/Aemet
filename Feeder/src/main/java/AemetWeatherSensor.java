import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

public class AemetWeatherSensor implements WeatherSensor {

    public List<Weather> readAreaWeather(Area area) {
        String url = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas";
        String response = getRequest(url);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        String data = jsonObject.get("datos").getAsString();
        String content = getRequest(data);
        JsonArray jsonElements = gson.fromJson(content, JsonArray.class);
        return getAreaWeather(area, jsonElements);
    }

    private List<Weather> getAreaWeather(Area area, JsonArray jsonElements) {
        return jsonElements.asList().stream()
                .filter(this::hasTemperature)
                .filter(jsonElement -> area.latMin() < jsonElement.getAsJsonObject().get("lat").getAsDouble() &&
                        jsonElement.getAsJsonObject().get("lat").getAsDouble() < area.latMax())
                .filter(jsonElement -> area.lonMin() < jsonElement.getAsJsonObject().get("lon").getAsDouble() &&
                        jsonElement.getAsJsonObject().get("lon").getAsDouble() < area.lonMax())
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

    private String getRequest(String url) {
        String apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZXJhcmRvbGVvcXVpbkBnbWFpbC5jb20iLCJqdGkiOiJlZjcxODk0MC1jMDk2LTQzMzEtYWUwOC1kZDgxNTNhZDMzYTciLCJpc3MiOiJBRU1FVCIsImlhdCI6MTY3MjY3NzQxOCwidXNlcklkIjoiZWY3MTg5NDAtYzA5Ni00MzMxLWFlMDgtZGQ4MTUzYWQzM2E3Iiwicm9sZSI6IiJ9.0rAmdDWFPBkUmfT5ss9R1zAYoedjdDZQMjaKm4WJuxc";
        try {
            return SSLHelper.getConnection(url)
                    .timeout(6000)
                    .ignoreContentType(true)
                    .header("accept", "application/json")
                    .header("api_key", apiKey)
                    .method(Connection.Method.GET)
                    .maxBodySize(0).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SSLHelper {
        static public Connection getConnection(String url) {
            return Jsoup.connect(url).sslSocketFactory(SSLHelper.socketFactory());
        }

        static private SSLSocketFactory socketFactory() {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                return sslContext.getSocketFactory();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException("Failed to create a SSL socket factory", e);
            }
        }
    }
}