package ktu.masters.core.handlers;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.common.collect.Lists;
import ktu.masters.dto.DatabaseType;
import ktu.masters.dto.QueryType;
import ktu.masters.exception.ApiException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;
import static ktu.masters.core.utils.Helper.CONSUMER_FUNCTION;
import static ktu.masters.dto.QueryType.GROUP;
import static ktu.masters.dto.QueryType.SEARCH;

public class CouchDBHandler implements DbHandler {
    private final CloudantClient dbConn;

    public CouchDBHandler(CloudantClient dbConn) {
        this.dbConn = dbConn;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.COUCH_DB;
    }

    @Override
    public void run(String colName, QueryType type, List<String> query, String sessionId) {
        Database database = dbConn.database(colName, true);
        List<Map> docs = database.query(query.get(0), Map.class).getDocs();
        if (SEARCH.equals(type)) {
            docs.forEach(CONSUMER_FUNCTION);
        } else if (GROUP.equals(type)) {
            Set<Object> seen = new HashSet<>();
            AtomicLong count = new AtomicLong(0L);
            docs.forEach(obj -> {
                Object val = obj.get(query.get(1));
                if (!seen.contains(val))
                    count.incrementAndGet();
                seen.add(val);
            });
        } else {
            throw new ApiException(500, "Unsupported query type for MONGO db - " + type);
        }
    }

    @Override
    public void reset(String colName, String fileName, String sessionId) {
        dbConn.deleteDB(colName);
        Database database = dbConn.database(colName, true);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            writeFromFile(br, database);
        } catch (FileNotFoundException notFounEx) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))))) {
                writeFromFile(br, database);
            } catch (IOException | ParseException IOEx) {
                throw new ApiException(500, IOEx, "Failed to load file for couchdb");
            }
        } catch (Exception e) {
            throw new ApiException(500, e, "Failed to load file for couchdb");
        }
    }

    private void writeFromFile(BufferedReader br, Database db) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        @SuppressWarnings("unchecked") List<Object> json = (List<Object>) parser.parse(br);
        Lists.partition(json, BATCH_SIZE).forEach(db::bulk);
    }
}
