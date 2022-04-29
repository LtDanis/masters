package ktu.masters.core.utils;

import com.aerospike.client.AerospikeClient;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ktu.masters.core.handlers.AerospikeDBHandler;
import ktu.masters.core.handlers.CouchDBHandler;
import ktu.masters.core.handlers.DbHandler;
import ktu.masters.core.handlers.MongoHandler;
import ktu.masters.dto.DatabaseType;
import ktu.masters.exception.ApiException;
import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@UtilityClass
public class HandlersHelper {
    public static final MongoDatabase MONGO_DB_CONN = initMongoDbConn();
    public static final CloudantClient COUCH_DB_CONN = initCouchConnection();
    public static final AerospikeClient AEROSPIKE_DB_CONN = new AerospikeClient("127.0.0.1", 3000);

    public static final MongoHandler MONGO_HANDLER = new MongoHandler(MONGO_DB_CONN);
    public static final CouchDBHandler COUCH_HANDLER = new CouchDBHandler(COUCH_DB_CONN);
    public static final AerospikeDBHandler AEROSPIKE_HANDLER = new AerospikeDBHandler(AEROSPIKE_DB_CONN);
    public static final List<DbHandler> DB_HANDLERS = List.of(MONGO_HANDLER, COUCH_HANDLER, AEROSPIKE_HANDLER);

    public static DbHandler findByType(DatabaseType type) {
        return DB_HANDLERS.stream()
                .filter(dbHandler -> type.equals(dbHandler.getType()))
                .findFirst()
                .orElseThrow(() -> new ApiException(500, "Didn't find any matching initializer for type - " + type));
    }

    private static MongoDatabase initMongoDbConn() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27888");
        return mongoClient.getDatabase("test");
    }

    private CloudantClient initCouchConnection() {
        try {
            return ClientBuilder.url(new URL("http://localhost:5984"))
                    .username("admin")
                    .password("couchdb")
                    .disableSSLAuthentication()
                    .build();
        } catch (MalformedURLException e) {
            throw new ApiException(500, e, "Failed to init couchdb connection");
        }
    }
}
