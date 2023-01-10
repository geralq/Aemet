import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;

public interface DatamartReader {

    String readMaxTemperature(String from, String to) throws SQLException, JsonProcessingException;

    String readMinTemperature(String from, String to) throws SQLException, JsonProcessingException;

}