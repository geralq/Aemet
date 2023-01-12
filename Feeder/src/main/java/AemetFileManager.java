import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AemetFileManager implements FileManager {
    public void createDatalakeDirectory() {
        File file = new File("./datalake");
        file.mkdir();
    }

    public void createFiles(List<Weather> weatherList) throws IOException {
        LocalDate todayDate = LocalDate.now();
        writeInFile(weatherList, todayDate, getBufferedWriter(todayDate), get(todayDate));

        LocalDate yesterdayDate = todayDate.minusDays(1);
        writeInFile(weatherList, yesterdayDate, getBufferedWriter(yesterdayDate), get(yesterdayDate));
    }

    private void writeInFile(List<Weather> weatherList, LocalDate date, BufferedWriter bufferedWriter, Set<Weather> weatherSet) throws IOException {
        weatherList.stream()
                .filter(weather -> weather.date().contains(date.toString()))
                .filter(weather -> !weatherSet.contains(weather))
                .forEach(weather -> {
                    try {
                        save(bufferedWriter, weather);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private BufferedWriter getBufferedWriter(LocalDate date) throws IOException {
        String formatedDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        return new BufferedWriter(new FileWriter("datalake/" + formatedDate + ".events", true));
    }

    private void save(BufferedWriter bufferedWriter, Weather weather) throws IOException {
        Gson gson = new Gson();
        bufferedWriter.write(gson.toJson(weather) + '\n');
    }

    private Set<Weather> get(LocalDate date) throws IOException {
        String formatedDate = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        BufferedReader bufferedReader = new BufferedReader(new FileReader("datalake/" + formatedDate + ".events"));
        String line;
        Set<Weather> weatherSet = new HashSet<>();
        while ((line = bufferedReader.readLine()) != null) {
            weatherSet.add(stringToWeather(line));
        }
        return weatherSet;
    }

    private Weather stringToWeather(String line) throws JsonProcessingException {
        return new ObjectMapper().readValue(line, Weather.class);
    }
}