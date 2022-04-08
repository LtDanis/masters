package ktu.masters.core.handlers.mongo;

import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.DB_CONN;

class MongoHandlerTest {
    @Test
    void testStart() {
        new MongoHandler(DB_CONN).reset("test", "/test.json");
    }

    @Test
    void testRunQueries() {
        new MongoHandler(DB_CONN).runQuery("test", "{ _id: \"5677d313fad7da08e362a512\" }");
    }
}
