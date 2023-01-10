import static spark.Spark.get;

public class WebService {
    private final DatamartReader datamartReader;

    public WebService(DatamartReader datamartReader) {
        this.datamartReader = datamartReader;
    }

    public void startAPI() {
        get("/v1/places/with-max-temperature", (req, res) -> {
            res.type("application/json");
            String from = req.queryParams("from");
            String to = req.queryParams("to");
            return datamartReader.readMaxTemperature(from, to);
        });
        get("/v1/places/with-min-temperature", (req, res) -> {
            res.type("application/json");
            String from = req.queryParams("from");
            String to = req.queryParams("to");
            return datamartReader.readMinTemperature(from, to);
        });
    }
}
