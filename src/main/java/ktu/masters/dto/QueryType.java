package ktu.masters.dto;

import ktu.masters.exception.ApiException;

import java.util.stream.Stream;

public enum QueryType {
    SEARCH, GROUP, JOIN;

    public static QueryType parse(String queryType) {
        return Stream.of(QueryType.values())
                .filter(v -> v.toString().equalsIgnoreCase(queryType))
                .findFirst()
                .orElseThrow(() -> new ApiException(400, "Couldn't find query by type"));
    }
}
