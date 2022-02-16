package ktu.masters.core.handlers.mongo;

import ktu.masters.core.handlers.DbHandler;
import ktu.masters.dto.Database;

import java.util.List;

public class MongoHandler implements DbHandler {
    @Override
    public Database getType() {
        return Database.MONGO;
    }

    @Override
    public void reset(String databaseName, String fileName) {
        List<String> commands = List.of(
                String.format("db.%s.remove({});", databaseName),
                String.format("use %s;", databaseName),
                String.format("mongoimport --jsonArray --db mydata --collection student --file F:\\students.json", databaseName)
        );
        // TODO run commands on container
    }
}
