package ktu.masters.core.handlers;

import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.MONGO_DB_CONN;

class MongoHandlerTest {
    private final MongoHandler mongoHandler = new MongoHandler(MONGO_DB_CONN);

    @Test
    void testStart() {
        mongoHandler.reset("test", "/test.json");
    }

    @Test
    void testRunQueries() {
        mongoHandler.runQuery("test", "{ _id: \"5677d313fad7da08e362a512\" }");
    }
}
