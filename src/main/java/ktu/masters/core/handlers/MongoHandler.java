package ktu.masters.core.handlers;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import ktu.masters.dto.DatabaseType;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

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
    public void reset(String colName, String fileName) {
        MongoCollection<Document> coll = dbCon.getCollection(colName);
        coll.drop(); //drop previous import
        List<InsertOneModel<Document>> docs = new ArrayList<>();
        try {
            bulkWrite(fileName, coll, docs);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void run(String colName, String query) {
        try (MongoCursor<Document> cursor = dbCon.getCollection(colName).find(Document.parse(query)).iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
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
        JSONParser parser = new JSONParser();
        @SuppressWarnings("unchecked") List<JSONObject> json = (List<JSONObject>) parser.parse(br);
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
        System.out.println("Inserted" + bulkWriteResult);
    }
}
