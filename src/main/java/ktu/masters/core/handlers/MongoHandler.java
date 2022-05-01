package ktu.masters.core.handlers;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import ktu.masters.dto.DatabaseType;
import ktu.masters.dto.Pair;
import ktu.masters.dto.QueryType;
import ktu.masters.exception.ApiException;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static java.util.Objects.requireNonNull;
import static ktu.masters.core.utils.Helper.CONSUMER_FUNCTION;
import static ktu.masters.dto.QueryType.*;

public class MongoHandler implements DbHandler {
    private final MongoDatabase dbCon;

    public MongoHandler(MongoDatabase dbCon) {
        this.dbCon = dbCon;
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MONGO;
    }

    @Override
    public void reset(String colName, String fileName, String sessionId) {
        MongoCollection<Document> coll = dbCon.getCollection(colName);
        coll.drop(); //drop previous import
        List<InsertOneModel<Document>> docs = new ArrayList<>();
        try {
            bulkWrite(fileName, coll, docs);
        } catch (Exception e) {
            throw new ApiException(500, e, "Failed to reset MongoDB");
        }
    }

    @Override
    public void run(String colName, QueryType type, List<String> query, String sessionId) {
        Set<Object> seen = new HashSet<>();
        List<Document> all = new ArrayList<>();
        AtomicLong count = new AtomicLong(0L);
        try (MongoCursor<Document> cursor = getCursor(colName, query).iterator()) {
            if (SEARCH.equals(type)) {
                while (cursor.hasNext()) {
                    CONSUMER_FUNCTION.accept(cursor.next());
                }
            } else if (GROUP.equals(type)) {
                while (cursor.hasNext()) {
                    Object val = cursor.next().get(query.get(1));
                    if (!seen.contains(val))
                        count.incrementAndGet();
                    seen.add(val);
                }
            } else if (JOIN.equals(type)) {
                while (cursor.hasNext()) {
                    all.add(cursor.next());
                }
                System.out.println(
                        all.stream()
                                .flatMap(obj1 -> all.stream()
                                        .filter(obj2 -> !Objects.equals(obj1.get("_id"), obj2.get("_id")))
                                        .filter(obj2 -> isEqualValues(obj1, query.get(1), obj2, query.get(2)))
                                        .map(obj2 -> new Pair<>(obj1, obj2)))
                                .count()
                );
            } else {
                throw new ApiException(500, "Unsupported query type for MONGO db - " + type);
            }
        }
    }

    private boolean isEqualValues(Document obj1, String key1, Document obj2, String key2) {
        Object v1 = obj1;
        for (String tempPath1 : List.of(key1.split("\\.")))
            v1 = ((Document) v1).get(tempPath1);
        Object v2 = obj2;
        for (String tempPath2 : List.of(key2.split("\\.")))
            v2 = ((Document) v2).get(tempPath2);
        return Objects.equals(v1, v2);
    }

    private FindIterable<Document> getCursor(String colName, List<String> query) {
        FindIterable<Document> findIterable = dbCon.getCollection(colName).find(Document.parse(query.get(0)));
        if (query.size() == 1)
            return findIterable;
        return findIterable.projection(fields(include(getFields(query))));
    }

    private String[] getFields(List<String> query) {
        return query.subList(1, query.size()).toArray(new String[]{});
    }

    private void bulkWrite(String fileName, MongoCollection<Document> coll, List<InsertOneModel<Document>> docs) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            writeFromFile(coll, br, docs);
        } catch (FileNotFoundException e) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(requireNonNull(this.getClass().getResourceAsStream(fileName))))) {
                writeFromFile(coll, br, docs);
            }
        }
    }

    private void writeFromFile(MongoCollection<Document> coll,
                               BufferedReader br,
                               List<InsertOneModel<Document>> docs) throws Exception {
        @SuppressWarnings("unchecked") List<JSONObject> json = (List<JSONObject>) new JSONParser().parse(br);
        Iterator<JSONObject> iterator = json.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            docs.add(new InsertOneModel<>(Document.parse(iterator.next().toJSONString())));
            count++;
            if (count == BATCH_SIZE) {
                singleBulkWrite(coll, docs);
                docs.clear();
                count = 0;
            }
        }
        if (count > 0)
            singleBulkWrite(coll, docs);
    }

    private void singleBulkWrite(MongoCollection<Document> coll, List<InsertOneModel<Document>> docs) {
        BulkWriteResult bulkWriteResult = coll.bulkWrite(docs, new BulkWriteOptions().ordered(false));
        System.out.println("Inserted " + bulkWriteResult);
    }
}
