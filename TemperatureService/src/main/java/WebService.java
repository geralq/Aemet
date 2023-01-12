import java.time.LocalDate;

import static spark.Spark.get;

public class WebService {
    private final AemetDatamartReader aemetDatamartReader;

    public WebService(AemetDatamartReader aemetDatamartReader) {
        this.aemetDatamartReader = aemetDatamartReader;
    }

    public void startAPI() {
        get("/v1/places/with-max-temperature", (req, res) -> {
            res.type("application/json");
            LocalDate from = LocalDate.parse(req.queryParams("from"));
            LocalDate to = LocalDate.parse(req.queryParams("to"));
            return aemetDatamartReader.getMaxTemperatures(from, to);
        });
        get("/v1/places/with-min-temperature", (req, res) -> {
            res.type("application/json");
            LocalDate from = LocalDate.parse(req.queryParams("from"));
            LocalDate to = LocalDate.parse(req.queryParams("to"));
            return aemetDatamartReader.getMinTemperatures(from, to);
        });
    }
}
