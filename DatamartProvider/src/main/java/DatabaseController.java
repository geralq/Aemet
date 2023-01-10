import java.sql.*;

public class DatabaseController {
    String databaseURL = "jdbc:sqlite:database.db/";
    Connection connection;

    {
        try {
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Statement statement;

    {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DatabaseController() throws SQLException {
        dropMaxTemperature();
        dropMinTemperature();
        this.databaseURL = databaseURL;
        this.connection = connection;
        this.statement = statement;
        connect();
        maxTemperature();
        minTemperature();
    }

    private void dropMaxTemperature() throws SQLException {
        String sql = "DROP TABLE IF EXISTS maxTemperature";
        statement.execute(sql);
    }

    private void dropMinTemperature() throws SQLException {
        String sql = "DROP TABLE IF EXISTS minTemperature";
        statement.execute(sql);
    }

    private void maxTemperature() throws SQLException {
        String maxTemp = "CREATE TABLE IF NOT EXISTS maxTemperature ("
                + "date TEXT NOT NULL,"
                + "time TEXT NOT NULL,"
                + "place TEXT NOT NULL,"
                + "station TEXT NOT NULL,"
                + "value REAL NOT NULL"
                + ")";
        statement.execute(maxTemp);
    }

    private void minTemperature() throws SQLException {
        String minTemp = "CREATE TABLE IF NOT EXISTS minTemperature ("
                + "date TEXT NOT NULL,"
                + "time TEXT NOT NULL,"
                + "place TEXT NOT NULL,"
                + "station TEXT NOT NULL,"
                + "value REAL NOT NULL"
                + ")";
        statement.execute(minTemp);
    }

    public void insertMaxTemperature(Weather weather) throws SQLException {
        String sql = "INSERT INTO maxTemperature(date, time, place, station, value)" +
                "VALUES(?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, weather.date().replaceAll("T.*", ""));
            pstmt.setString(2, weather.date().replaceAll(".*T", ""));
            pstmt.setString(3, weather.ubi());
            pstmt.setString(4, weather.id());
            pstmt.setDouble(5, weather.tempmax());
            pstmt.executeUpdate();
        }
    }

    public void insertMinTemperature(Weather weather) throws SQLException {
        String sql = "INSERT INTO minTemperature(date, time, place, station, value)" +
                "VALUES(?,?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, weather.date().replaceAll("T.*", ""));
            pstmt.setString(2, weather.date().replaceAll(".*T", ""));
            pstmt.setString(3, weather.ubi());
            pstmt.setString(4, weather.id());
            pstmt.setDouble(5, weather.tempmin());
            pstmt.executeUpdate();
        }
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

}
