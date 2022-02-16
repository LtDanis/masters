package ktu.masters.dto;

import lombok.Value;

import java.util.List;

@Value
public class SessionRequest {
    String userId;
    String fileName;
    String databaseName;
    boolean reloadDatabase;
    List<Database> types;
}
