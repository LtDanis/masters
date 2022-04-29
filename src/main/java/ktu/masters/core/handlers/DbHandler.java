package ktu.masters.core.handlers;

import ktu.masters.dto.DatabaseType;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface DbHandler {
    int BATCH_SIZE = 100;

    DatabaseType getType();

    void reset(String colName, String fileName, String sessionId);

    void run(String colName, List<String> query, String sessionId);

    default List<Long> runQuery(String colName, List<String> query, int times, String sessionId) {
        return IntStream.range(0, times)
                .mapToObj(index -> singleRun(colName, query, sessionId))
                .collect(toList());
    }

    private long singleRun(String colName, List<String> query, String sessionId) {
        long start = System.nanoTime();
        run(colName, query,
                sessionId);
        return System.nanoTime() - start;
    }
}
