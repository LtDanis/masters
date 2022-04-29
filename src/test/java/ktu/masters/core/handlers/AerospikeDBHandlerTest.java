package ktu.masters.core.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktu.masters.core.handlers.QueriesHelper.initKeys;
import static ktu.masters.core.utils.HandlersHelper.AEROSPIKE_DB_CONN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AerospikeDBHandlerTest {
    private static final String SESSION_ID = "sessionId";
    private final AerospikeDBHandler aerospikeDBHandler = new AerospikeDBHandler(AEROSPIKE_DB_CONN);

    @BeforeEach
    void setUp() {
        initKeys(aerospikeDBHandler, SESSION_ID);
    }

    @Test
    void testStart() {
        assertDoesNotThrow(() -> aerospikeDBHandler.reset("test", "/test.json", SESSION_ID));
    }

    @Test
    void testRunQueries() {
        assertDoesNotThrow(() -> aerospikeDBHandler.runQuery("test", List.of("$.*"), 1, SESSION_ID));
    }
}
