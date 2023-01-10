import com.google.gson.Gson;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AemetDatamartReader {

    public String getMaxTemperatures(String from, String to) throws SQLException {
        String sql = "SELECT * FROM maxTemperature";
        Connection conn = connect();
        Statement statement = conn.createStatement();
        List<Response> responseList = contentToResponse(statement.executeQuery(sql));
        return new Gson().toJson(filterByDate(from, to, responseList));
    }

    public String getMinTemperatures(String from, String to) throws SQLException {
        String sql = "SELECT * FROM minTemperature";
        Connection conn = connect();
        Statement statement = conn.createStatement();
        List<Response> responseList = contentToResponse(statement.executeQuery(sql));
        return new Gson().toJson(filterByDate(from, to, responseList));
    }

    private Connection connect() {
        String databaseURL = "jdbc:sqlite:database.db/";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private List<Response> contentToResponse(ResultSet resultSet) throws SQLException {
        List<Response> responses = new ArrayList<>();
        while (resultSet.next()) {
            responses.add(new Response(
                    LocalDate.parse(resultSet.getString("date")),
                    resultSet.getString("time"),
                    resultSet.getString("place"),
                    resultSet.getString("station"),
                    resultSet.getDouble("value")));
        }
        return responses;
    }

    private List<Map<String, String>> filterByDate(String from, String to, List<Response> responseList) {
        List<Response> responses = responseList.stream()
                .filter(response -> response.date().isAfter(LocalDate.parse(from).minusDays(1)))
                .filter(response -> response.date().isBefore(LocalDate.parse(to).plusDays(1))).toList();
        return getMaps(responses);
    }

    private List<Map<String, String>> getMaps(List<Response> responses) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Response response : responses) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("DATE", response.date().toString());
            map.put("TIME", response.time());
            map.put("PLACE", response.place());
            map.put("STATION", response.station());
            map.put("VALUE", response.value().toString());
            mapList.add(map);
        }
        return mapList;
    }
}
