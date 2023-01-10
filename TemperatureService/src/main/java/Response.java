import java.time.LocalDate;

public record Response(LocalDate date, String time, String place, String station, Double value) {
}