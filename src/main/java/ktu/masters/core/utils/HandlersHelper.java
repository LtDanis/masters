package ktu.masters.core.utils;

import ktu.masters.core.handlers.DbHandler;
import ktu.masters.core.handlers.mongo.MongoHandler;
import ktu.masters.dto.Database;
import ktu.masters.exception.ApiException;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class HandlersHelper {
    private static final List<DbHandler> DB_HANDLERS = List.of(
            new MongoHandler()
    );

    public static DbHandler findByType(Database type) {
        return DB_HANDLERS.stream()
                .filter(dbHandler -> type.equals(dbHandler.getType()))
                .findFirst()
                .orElseThrow(() -> new ApiException(500, "Didn't find any matching initializer for type - " + type));
    }
}
