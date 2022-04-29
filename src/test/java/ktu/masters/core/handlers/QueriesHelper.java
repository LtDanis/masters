package ktu.masters.core.handlers;

import java.util.Set;

public final class QueriesHelper {
    private QueriesHelper() {
    }

    public static void initKeys(AerospikeDBHandler aerospikeDBHandler, String sessId) {
        aerospikeDBHandler.saveKeys(sessId, Set.of());
    }
}
