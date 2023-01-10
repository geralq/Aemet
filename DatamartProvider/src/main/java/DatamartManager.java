import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class DatamartManager {
    private final File datalake = new File("./datalake");

    public List<Weather> findMaxTemperatures() throws IOException {
        List<Weather> datamartMaxTemp = new ArrayList<>();
        for (File file : Objects.requireNonNull(datalake.listFiles())) {
            List<Weather> list = readFile(file.getName());
            Weather maxtemp = list.stream()
                    .max(Comparator.comparing(Weather::tempmax))
                    .get();
            datamartMaxTemp.add(maxtemp);
        }
        return datamartMaxTemp;
    }

    public List<Weather> findMinTemperatures() throws IOException {
        List<Weather> datamartMinTemp = new ArrayList<>();
        for (File file : Objects.requireNonNull(datalake.listFiles())) {
            List<Weather> list = readFile(file.getName());
            Weather maxtemp = list.stream()
                    .min(Comparator.comparing(Weather::tempmin))
                    .get();
            datamartMinTemp.add(maxtemp);
        }
        return datamartMinTemp;
    }

    private List<Weather> readFile(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("./datalake/" + fileName));
        String line;
        List<Weather> weatherList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            weatherList.add(stringToWeather(line));
        }
        return weatherList;
    }

    private Weather stringToWeather(String line) throws JsonProcessingException {
        return new ObjectMapper().readValue(line, Weather.class);
    }
}