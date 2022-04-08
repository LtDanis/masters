package ktu.masters.core;

import ktu.masters.dto.DatabaseType;
import ktu.masters.dto.DbQueryResult;
import ktu.masters.dto.QueryType;
import ktu.masters.dto.SessionData;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SessionDatabase {
    private static final Map<String, SessionData> SESSION_DATA_MAP = new ConcurrentHashMap<>();

    public static void saveTimeTaken(String sessionId, DbQueryResult queryResult) {
        SessionData sessionData = SESSION_DATA_MAP.computeIfAbsent(sessionId, id -> new SessionData());
        sessionData.getResults().add(queryResult);
    }

    public static void printByDb(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        if (isNull(sessionData) || sessionData.getResults().isEmpty())
            throw new IllegalStateException("No session found with id - " + sessionId);
        Map<DatabaseType, List<DbQueryResult>> collect = sessionData.getResults().stream()
                .collect(Collectors.groupingBy(DbQueryResult::getDb, toList()));
        collect.forEach((key, value) -> {
            System.out.printf("Result times in milliseconds for %s%n", key);
            value.forEach(result ->
                    System.out.printf("  %s %s -> %s%n", result.getType(), result.getName(), reformatTimes(result.getTimesTaken())));
        });
    }

    public static void printByQuery(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        if (isNull(sessionData) || sessionData.getResults().isEmpty())
            throw new IllegalStateException("No session found with id - " + sessionId);
        Map<QueryKey, List<DbQueryResult>> collect = sessionData.getResults().stream()
                .collect(Collectors.groupingBy(QueryKey::of, toList()));
        collect.forEach((key, value) -> {
            System.out.printf("Result times in milliseconds for %s %s %n", key.getName(), key.getType());
            value.forEach(result ->
                    System.out.printf("  %s -> %s%n", result.getDb(), reformatTimes(result.getTimesTaken())));
        });
    }

    private static String reformatTimes(List<Long> times) {
        return times.stream()
                .map(time -> time / 1000_000f)
                .map(f -> String.format("%.3f", f))
                .collect(joining(", "));
    }

    @Value
    private static class QueryKey {
        String name;
        QueryType type;

        static QueryKey of(DbQueryResult result) {
            return new QueryKey(result.getName(), result.getType());
        }
    }
}
