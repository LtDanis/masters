package ktu.masters.core.handlers;

import ktu.masters.dto.DatabaseType;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface DbHandler {
    int BATCH_SIZE = 100;

    DatabaseType getType();

    void reset(String colName, String fileName);

    void run(String colName, String query);

    default List<Long> runQuery(String colName, String query, int times) {
        return IntStream.range(0, times)
                .mapToObj(index -> singleRun(colName, query))
                .collect(toList());
    }

    private long singleRun(String colName, String query) {
        long start = System.nanoTime();
        run(colName, query);
        return System.nanoTime() - start;
    }
}
