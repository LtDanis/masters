package ktu.masters.core.handlers;

import ktu.masters.dto.Database;

public interface DbHandler {
    Database getType();

    void reset(String databaseName, String fileName);
}
