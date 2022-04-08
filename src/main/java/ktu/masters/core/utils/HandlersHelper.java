package ktu.masters.core.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ktu.masters.core.handlers.DbHandler;
import ktu.masters.core.handlers.mongo.MongoHandler;
import ktu.masters.dto.Database;
import ktu.masters.exception.ApiException;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class HandlersHelper {
    public static final MongoDatabase DB_CONN = initDbConn();

    private static final List<DbHandler> DB_HANDLERS = List.of(
            new MongoHandler(DB_CONN)
    );

    public static DbHandler findByType(Database type) {
        return DB_HANDLERS.stream()
                .filter(dbHandler -> type.equals(dbHandler.getType()))
                .findFirst()
                .orElseThrow(() -> new ApiException(500, "Didn't find any matching initializer for type - " + type));
    }

    private static MongoDatabase initDbConn() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27888");
        return mongoClient.getDatabase("test");
    }
}
