package ktu.masters.core.handlers;

import ktu.masters.dto.Database;

public interface DbHandler {
    Database getType();

    void reset(String colName, String fileName);

    long runQuery(String colName, String query);
}
