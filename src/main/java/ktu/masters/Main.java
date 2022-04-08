package ktu.masters;

import ktu.masters.core.utils.JsonTransformer;
import ktu.masters.core.utils.RouteController;
import ktu.masters.exception.ApiException;
import spark.Spark;

import static spark.Spark.exception;

public class Main {
    private static final int PORT = 4567;

    public static void main(String[] args) {
        new Main().start(PORT);
    }

    public void start(int port) {
        Spark.port(port);
        Spark.init();
        Spark.defaultResponseTransformer(new JsonTransformer());
        Spark.after((req, res) -> res.type("application/json"));

        Spark.post("/start", RouteController::handleSessionInit);
        Spark.post("/run", RouteController::runQueries);
        Spark.get("/results/:sessionId", RouteController::readResults);

        exception(ApiException.class, (exception, request, response) -> {
            response.status(exception.getStatus());
            response.body(exception.toJson());
        });
    }
}
