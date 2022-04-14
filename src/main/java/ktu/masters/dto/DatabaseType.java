package ktu.masters.dto;

import ktu.masters.exception.ApiException;

import java.util.stream.Stream;

public enum DatabaseType {
    MONGO,
    COUCH_DB,
    AEROSPIKE;

    public static DatabaseType parse(String dbType) {
        return Stream.of(DatabaseType.values())
                .filter(v -> v.toString().equalsIgnoreCase(dbType))
                .findFirst()
                .orElseThrow(() -> new ApiException(400, "Couldn't find database by type"));
    }
}
