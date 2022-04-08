package ktu.masters.dto;

import lombok.Value;

import java.util.List;

@Value
public class DbQueryResult {
    String name;
    QueryType type;
    DatabaseType db;
    List<String> timesTaken;
}
