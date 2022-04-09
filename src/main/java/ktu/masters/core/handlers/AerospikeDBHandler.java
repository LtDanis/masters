package ktu.masters.core.handlers;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.documentapi.AerospikeDocumentClient;
import com.aerospike.documentapi.DocumentApiException;
import com.aerospike.documentapi.JsonPathParser.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ktu.masters.dto.DatabaseType;
import ktu.masters.exception.ApiException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class AerospikeDBHandler implements DbHandler {
    private static final Key KEY = new Key("test", "test", "test");

    private final AerospikeDocumentClient documentClient;

    public AerospikeDBHandler(AerospikeClient dbConn) {
        this.documentClient = new AerospikeDocumentClient(dbConn);
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.AEROSPIKE;
    }

    @Override
    public void reset(String colName, String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            writeFromFile(br, colName);
        } catch (FileNotFoundException notFounEx) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))))) {
                writeFromFile(br, colName);
            } catch (Exception e) {
                throw new ApiException(500, e, "Failed to load file for Aerospike");
            }
        } catch (Exception e) {
            throw new ApiException(500, e, "Failed to load file for Aerospike");
        }
    }

    private void writeFromFile(BufferedReader br, String colName) throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree(br);
        documentClient.put(KEY, colName, jsonNode);
    }

    @Override
    public void run(String colName, String query) {
        try {
            @SuppressWarnings("unchecked") List<Map<String, Object>> objectsFromDB =
                    (List<Map<String, Object>>) documentClient.get(KEY, colName, query);
            objectsFromDB.forEach(System.out::println);
        } catch (JsonParseException | JsonProcessingException | DocumentApiException e) {
            throw new ApiException(500, e, "Failed to fetch Aerospike query");
        }
    }
}
