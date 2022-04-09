package ktu.masters.core.handlers;

import ktu.masters.exception.ApiException;
import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.MONGO_DB_CONN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MongoHandlerTest {
    private final MongoHandler mongoHandler = new MongoHandler(MONGO_DB_CONN);

    @Test
    void testStart() {
        assertDoesNotThrow(() -> mongoHandler.reset("test", "/NO_BENCH_TEST.json"));
    }

    @Test
    void testStart_nonExistingFile() {
        assertThatThrownBy(() -> mongoHandler.reset("test", "/notFound.json"))
                .isInstanceOf(ApiException.class);
    }


    @Test
    void testRunQueries() {
        assertDoesNotThrow(() -> mongoHandler.runQuery("test", "{ _id: \"5677d313fad7da08e362a512\" }", 1));
    }
}
