package ktu.masters.dto;

import lombok.Value;

import java.util.List;

@Value
public class RunQueriesRequest {
    String sessionId;
    String colName;
    int numberOfRuns;
    List<QuerySet> querySet;

    @Value
    public static class QuerySet {
        String name;
        QueryType type;
        List<Query> queries;
    }

    @Value
    public static class Query {
        DatabaseType db;
        String query;
    }
}
