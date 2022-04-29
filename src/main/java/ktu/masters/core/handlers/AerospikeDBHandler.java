package ktu.masters.core.handlers;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.documentapi.AerospikeDocumentClient;
import com.aerospike.documentapi.DocumentApiException;
import com.aerospike.documentapi.JsonPathParser.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.Lists;
import ktu.masters.dto.DatabaseType;
import ktu.masters.exception.ApiException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

public class AerospikeDBHandler implements DbHandler {
    private static final String FILENAME = "store";
    private final AerospikeDocumentClient documentClient;

    public AerospikeDBHandler(AerospikeClient dbConn) {
        this.documentClient = new AerospikeDocumentClient(dbConn);
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.AEROSPIKE;
    }

    @Override
    public void reset(String colName, String fileName, String sessionId) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            writeFromFile(br, colName, sessionId);
        } catch (FileNotFoundException notFounEx) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))))) {
                writeFromFile(br, colName, sessionId);
            } catch (Exception e) {
                throw new ApiException(500, e, "Failed to load file for Aerospike");
            }
        } catch (Exception e) {
            throw new ApiException(500, e, "Failed to load file for Aerospike");
        }
    }

    private void writeFromFile(BufferedReader br, String colName, String sessionId) throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree(br);
        List<JsonNode> nodes = StreamSupport.stream(jsonNode.spliterator(), false)
                .collect(Collectors.toList());
        List<List<JsonNode>> partitioned = Lists.partition(nodes, BATCH_SIZE);
        Set<String> keys = new HashSet<>();
        for (int index = 0; index < partitioned.size(); index++) {
            String keyValue = "test" + index;
            Key key = createKey(keyValue);
            keys.add(keyValue);
            documentClient.put(key, colName, toNode(partitioned.get(index)));
        }
        saveKeys(sessionId, keys);
    }

    void saveKeys(String sessionId, Set<String> keys) {
        MVStore s = MVStore.open(FILENAME);
        MVMap<String, Set<String>> map = s.openMap("data");
        map.put(sessionId, keys);
        s.close();
    }

    private Set<Key> loadKeys(String sessionId) {
        MVStore s = MVStore.open(FILENAME);
        MVMap<String, Set<String>> map = s.openMap("data");
        Set<String> keyValues = map.get(sessionId);
        s.close();
        if (isNull(keyValues))
            throw new ApiException(400, "Aerospike cannot run queries without loaded Keys");
        return keyValues.stream()
                .map(this::createKey)
                .collect(toSet());
    }

    private Key createKey(String keyValue) {
        return new Key("test", "test", keyValue);
    }

    private JsonNode toNode(List<JsonNode> partition) {
        JsonNodeFactory factory = new JsonNodeFactory(true);
        ArrayNode newNode = new ArrayNode(factory, partition.size());
        partition.forEach(newNode::add);
        return newNode;
    }

    @Override
    public void run(String colName, String query, String sessionId) {
        try {
            if (isNull(sessionId))
                throw new ApiException(400, "Aerospike cannot run queries with session ID");
            for (Key key : loadKeys(sessionId)) {
                @SuppressWarnings("unchecked") List<Map<String, Object>> objectsFromDB =
                        (List<Map<String, Object>>) documentClient.get(key, colName, query);
                objectsFromDB.forEach(System.out::println);
            }
        } catch (JsonParseException | JsonProcessingException | DocumentApiException e) {
            throw new ApiException(500, e, "Failed to fetch Aerospike query");
        }
    }
}
