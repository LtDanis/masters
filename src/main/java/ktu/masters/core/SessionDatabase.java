package ktu.masters.core;

import ktu.masters.dto.*;
import ktu.masters.exception.ApiException;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class SessionDatabase {
    private static final Map<String, RunQueriesRequest> QUERIES_DATA_MAP = new ConcurrentHashMap<>();
    private static final Map<String, SessionData> SESSION_DATA_MAP = new ConcurrentHashMap<>();

    public static void saveRequest(RunQueriesRequest request) {
        QUERIES_DATA_MAP.put(request.getSessionId(), request);
    }

    public static RunQueriesRequest getRequest(String sessionId) {
        RunQueriesRequest request = QUERIES_DATA_MAP.get(sessionId);
        if (isNull(request))
            throw new ApiException(400, "No session found with id - " + sessionId);
        return request;
    }

    public static void reset(String sessionId) {
        SESSION_DATA_MAP.remove(sessionId);
    }

    public static void saveTimeTaken(String sessionId, DbQueryResult queryResult) {
        SessionData sessionData = SESSION_DATA_MAP.computeIfAbsent(sessionId, id -> new SessionData());
        sessionData.getResults().add(queryResult);
    }

    public static void printAverageTimeTaken(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        Map<QueryType, List<DbQueryResult>> collect = sessionData.getResults().stream()
                .collect(Collectors.groupingBy(DbQueryResult::getType, toList()));
        collect.forEach((key, value) -> {
            System.out.printf("Result times in nanos for %s%n", key);
            value.forEach(result ->
                    System.out.printf("  %s %s -> %s (%ss)%n",
                            result.getDb(), result.getName(), result.getAvg(), nanosToSeconds(result.getAvg())));
        });
    }

    public static void printByDb(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        if (isNull(sessionData) || sessionData.getResults().isEmpty())
            throw new ApiException(400, "No session found with id - " + sessionId);
        Map<DatabaseType, List<DbQueryResult>> collect = sessionData.getResults().stream()
                .collect(Collectors.groupingBy(DbQueryResult::getDb, toList()));
        collect.forEach((key, value) -> {
            System.out.printf("Result times in milliseconds for %s%n", key);
            value.forEach(result ->
                    System.out.printf("  %s %s -> %s%n", result.getType(), result.getName(), result.getTimesTaken()));
        });
    }

    public static void printByQuery(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        if (isNull(sessionData) || sessionData.getResults().isEmpty())
            throw new ApiException(400, "No session found with id - " + sessionId);
        Map<QueryKey, List<DbQueryResult>> collect = sessionData.getResults().stream()
                .collect(Collectors.groupingBy(QueryKey::of, toList()));
        collect.forEach((key, value) -> {
            System.out.printf("Result times in milliseconds for %s %s %n", key.getName(), key.getType());
            value.forEach(result ->
                    System.out.printf("  %s -> %s%n", result.getDb(), result.getTimesTaken()));
        });
    }

    public static List<String> reformatTimes(List<Long> times) {
        return times.stream()
                .map(time -> time / 1000_000f)
                .map(f -> String.format("%.3f", f))
                .collect(Collectors.toList());
    }

    private static String nanosToSeconds(long nanos) {
        return String.format("%.6f", nanos / 1000_000_000f);
    }

    public static List<DbQueryResult> getResultsList(String sessionId) {
        SessionData sessionData = SESSION_DATA_MAP.get(sessionId);
        if (isNull(sessionData) || sessionData.getResults().isEmpty())
            throw new ApiException(400, "No session found with id - " + sessionId);
        return sessionData.getResults();
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
