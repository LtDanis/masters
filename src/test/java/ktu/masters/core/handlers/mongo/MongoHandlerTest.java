package ktu.masters.core.handlers.mongo;

import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.DB_CONN;

class MongoHandlerTest {
    @Test
    void test() {
        new MongoHandler(DB_CONN).reset("test", "/test.json");
    }
}
