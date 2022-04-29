package ktu.masters.core.handlers;

import ktu.masters.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktu.masters.core.utils.HandlersHelper.MONGO_DB_CONN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MongoHandlerTest {
    private final MongoHandler mongoHandler = new MongoHandler(MONGO_DB_CONN);

    @Test
    void testStart() {
        assertDoesNotThrow(() -> mongoHandler.reset("test", "/test.json", "sessionId"));
    }

    @Test
    void testStart_nonExistingFile() {
        assertThatThrownBy(() -> mongoHandler.reset("test", "/notFound.json", "sessionId"))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> (ApiException) ex)
                .extracting(ApiException::getMessage)
                .isEqualTo("Failed to reset MongoDB");
    }


    @Test
    void testRunQueries() {
        assertDoesNotThrow(() -> mongoHandler.runQuery("test", List.of("{ _id: \"5677d313fad7da08e362a512\" }"), 1, "sessionId"));
    }
}
