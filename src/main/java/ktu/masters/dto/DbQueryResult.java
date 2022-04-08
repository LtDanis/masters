package ktu.masters.dto;

import lombok.Value;

@Value
public class DbQueryResult {
    String name;
    QueryType type;
    DatabaseType db;
    long timeTaken;
}
