package ktu.masters.core.handlers;

import ktu.masters.dto.DatabaseType;

public interface DbHandler {
    int BATCH_SIZE = 100;

    DatabaseType getType();

    void reset(String colName, String fileName);

    void run(String colName, String query);

    default long runQuery(String colName, String query) {
        long start = System.nanoTime();
        run(colName, query);
        return System.nanoTime() - start;
    }
}
