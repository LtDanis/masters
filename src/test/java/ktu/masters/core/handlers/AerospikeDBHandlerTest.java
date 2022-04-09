package ktu.masters.core.handlers;

import org.junit.jupiter.api.Test;

import static ktu.masters.core.utils.HandlersHelper.AEROSPIKE_DB_CONN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AerospikeDBHandlerTest {
    private final AerospikeDBHandler aerospikeDBHandler = new AerospikeDBHandler(AEROSPIKE_DB_CONN);

    @Test
    void testStart() {
        assertDoesNotThrow(() -> aerospikeDBHandler.reset("test", "/test.json"));
    }

    @Test
    void testRunQueries() {
        assertDoesNotThrow(() -> aerospikeDBHandler.runQuery("test", "$.*", 1));
    }
}
