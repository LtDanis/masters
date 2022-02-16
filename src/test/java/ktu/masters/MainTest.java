package ktu.masters;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import ktu.masters.core.RouteFactory;
import ktu.masters.core.utils.RouteController;
import ktu.masters.dto.Database;
import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktu.masters.core.utils.JsonTransformer.GSON;
import static org.assertj.core.api.Assertions.assertThat;

class MainTest {
    private static final int TEST_PORT = 4569;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    private static final SessionResponse SESSION_RESPONSE = new SessionResponse("ASFLJASLFGA");

    @BeforeAll
    static void beforeAll() {
        RouteController.setFactory(new RouteFactory(
                req -> SESSION_RESPONSE
        ));
        new Main().start(TEST_PORT);
    }

    @Test
    void acceptanceTest_sessionStarted() throws UnirestException {
        String body = GSON.toJson(new SessionRequest(List.of(Database.MONGO)));
        String expectedResponse = GSON.toJson(SESSION_RESPONSE);

        HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/start")
                .body(body)
                .asJson();

        assertThat(response.getBody().toString()).isEqualTo(expectedResponse);
    }
}
