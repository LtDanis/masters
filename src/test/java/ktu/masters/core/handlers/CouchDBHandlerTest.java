package ktu.masters.core.handlers;

import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.COUCH_DB_CONN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CouchDBHandlerTest {
    private final CouchDBHandler couchDBHandler = new CouchDBHandler(COUCH_DB_CONN);

    @Test
    void testStart() {
        assertDoesNotThrow(() -> couchDBHandler.reset("test", "/test.json", "sessionId"));
    }

    @Test
    void testRunQueries() {
        assertDoesNotThrow(() -> couchDBHandler.runQuery("test", couchQuery(), 1, "sessionId"));
    }

    static String couchQuery() {
        return "{\n" +
                "    \"selector\": {\n" +
                "        \"_id\": \"5677d313fad7da08e362a512\"" +
                "    },\n" +
                "    \"fields\": [\"_id\", \"_rev\"],\n" +
                "    \"limit\": 1,\n" +
                "    \"skip\": 0,\n" +
                "    \"execution_stats\": true\n" +
                "}";
    }
}
