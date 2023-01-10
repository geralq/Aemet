import static spark.Spark.get;

public class WebService {
    private final AemetDatamartReader aemetDatamartReader;

    public WebService(AemetDatamartReader aemetDatamartReader) {
        this.aemetDatamartReader = aemetDatamartReader;
    }


    public void startAPI() {
        get("/v1/places/with-max-temperature", (req, res) -> {
            res.type("application/json");
            String from = req.queryParams("from");
            String to = req.queryParams("to");
            return aemetDatamartReader.readMaxTemperature(from, to);
        });
        get("/v1/places/with-min-temperature", (req, res) -> {
            res.type("application/json");
            String from = req.queryParams("from");
            String to = req.queryParams("to");
            return aemetDatamartReader.readMinTemperature(from, to);
        });
    }
}
