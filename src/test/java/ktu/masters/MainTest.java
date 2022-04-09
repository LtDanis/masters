package ktu.masters;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import ktu.masters.core.RouteFactory;
import ktu.masters.core.SessionDatabase;
import ktu.masters.core.utils.RouteController;
import ktu.masters.dto.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktu.masters.core.utils.JsonTransformer.GSON;
import static org.assertj.core.api.Assertions.assertThat;

class MainTest {
    private static final int TEST_PORT = 4569;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    private static final SessionResponse SESSION_RESPONSE = new SessionResponse("USER-2022");

    @BeforeAll
    static void beforeAll() {
        RouteController.setFactory(new RouteFactory(
                req -> SESSION_RESPONSE,
                req -> SESSION_RESPONSE
        ));
        new Main().start(TEST_PORT);
    }

    @Test
    void acceptanceTest_sessionStarted() throws UnirestException {
        SessionRequest sessionRequest =
                new SessionRequest("USER", "test1", "col1", true, List.of(DatabaseType.MONGO));

        HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/start")
                .body(GSON.toJson(sessionRequest))
                .asJson();

        assertThat(response.getBody().toString()).isEqualTo(GSON.toJson(SESSION_RESPONSE));
    }

    @Test
    void acceptanceTest_queriesRun() throws UnirestException {
        RunQueriesRequest sessionRequest = new RunQueriesRequest("ABC", "test", 1, List.of());

        HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/run")
                .body(GSON.toJson(sessionRequest))
                .asJson();

        assertThat(response.getBody().toString()).isEqualTo(GSON.toJson(SESSION_RESPONSE));
    }

    @Test
    void acceptanceTest_getResponse() throws UnirestException {
        DbQueryResult queryResult = new DbQueryResult("q1", QueryType.SEARCH, DatabaseType.MONGO, List.of("1"));
        SessionDatabase.saveTimeTaken("ABC", queryResult);

        HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/results/ABC").asJson();

        JSONObject queryResultFromDb = (JSONObject) response.getBody().getArray().get(0);
        assertThat(queryResultFromDb.get("name")).isEqualTo(queryResult.getName());
    }

    @Test
    void acceptanceTest_getNonExistingResponse() throws UnirestException {
        DbQueryResult queryResult = new DbQueryResult("q1", QueryType.SEARCH, DatabaseType.MONGO, List.of("1"));
        SessionDatabase.saveTimeTaken("NOT_FOUND", queryResult);

        HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/results/ABC").asJson();

        assertThat(response.getStatus()).isEqualTo(400);
    }
}
