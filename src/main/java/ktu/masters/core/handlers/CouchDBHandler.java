package ktu.masters.core.handlers;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import ktu.masters.dto.DatabaseType;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

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
    public void run(String colName, String query) {
        Database database = dbConn.database(colName, true);
        List<Object> docs = database.query(query, Object.class).getDocs();
        for (Object item : docs) {
            System.out.println(item);
        }

    }

    @Override
    public void reset(String colName, String fileName) {
        dbConn.deleteDB(colName);
        Database database = dbConn.database(colName, true);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            writeFromFile(br, database);
        } catch (FileNotFoundException notFounEx) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))))) {
                writeFromFile(br, database);
            } catch (IOException | ParseException IOEx) {
                throw new IllegalStateException(IOEx);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeFromFile(BufferedReader br, Database db) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        @SuppressWarnings("unchecked") List<Object> json = (List<Object>) parser.parse(br);
        for (Object item : json)
            db.post(item);
    }
}
