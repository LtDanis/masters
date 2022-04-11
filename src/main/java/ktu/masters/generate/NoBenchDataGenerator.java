package ktu.masters.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ktu.masters.exception.ApiException;
import lombok.Value;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;
import static ktu.masters.generate.Keywords.KEYWORDS;

public class NoBenchDataGenerator {
    private final AtomicBoolean bool;
    private final Random random;

    public NoBenchDataGenerator() {
        this.bool = new AtomicBoolean(true);
        this.random = new Random(System.nanoTime());
    }

    public void generateAndSave(int numOfItems, String fileName) {
        List<JsonNode> items = generate(numOfItems);
        try (OutputStreamWriter file = new FileWriter(String.format("./src/main/resources/%s.json", fileName))) {
            file.write(JSONArray.toJSONString(items));
        } catch (IOException e) {
            throw new ApiException(500, e, "Failed to save generated NO_BENCH data");
        }
    }

    public List<JsonNode> generate(int numOfItems) {
        List<StrCache> cache = createCachedDataForGeneration(numOfItems);
        List<Cluster> clusters = prepareClusters();
        return IntStream.range(0, numOfItems).parallel()
                .mapToObj(index -> createEntity(numOfItems, index, cache))
                .map(entity -> insertSparseAttr(entity, clusters))
                .collect(toList());
    }

    private List<StrCache> createCachedDataForGeneration(int numOfItems) {
        return IntStream.range(0, numOfItems)
                .mapToObj(index -> createCacheData())
                .collect(toList());
    }

    private StrCache createCacheData() {
        return new StrCache(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
    }

    private List<Cluster> prepareClusters() {
        List<String> dataPool = IntStream.range(0, 10)
                .mapToObj(index -> index + "-" + UUID.randomUUID())
                .collect(toList());
        return IntStream.range(0, 100)
                .mapToObj(index -> createCluster(index, dataPool))
                .collect(toList());
    }

    private Cluster createCluster(int index, List<String> dataPool) {
        List<String> clusterAttributes = IntStream.range(0, 10)
                .mapToObj(__ -> dataPool.get(random.nextInt(dataPool.size())))
                .collect(toList());
        return new Cluster(index, clusterAttributes);
    }

    private NoBenchEntity createEntity(int numOfItems, int index, List<StrCache> cache) {
        StrCache current = cache.get(index);
        boolean currentBool = bool.getAndSet(!bool.get());
        return new NoBenchEntity(current.getStr1(),
                current.getStr2(),
                index,
                currentBool,
                calcDyn1(index, current.getStr1()),
                calcDyn2(index, current.getStr1(), currentBool),
                createArr(),
                calcNested(numOfItems, index, cache),
                index % 1000
        );
    }

    private String[] createArr() {
        return LongStream.range(0, 7)
                .mapToObj(index -> findRandomKeyword())
                .toArray(String[]::new);
    }

    private String findRandomKeyword() {
        int index = random.nextInt(KEYWORDS.size());
        return KEYWORDS.get(index);
    }

    private NestedObj calcNested(int numOfItems, int num, List<StrCache> cache) {
        int n = random.nextInt(numOfItems);
        String nestedStr = cache.get(n).getStr1();
        int nestedNum = num == n ? num + 1 : n;
        return new NestedObj(nestedStr, nestedNum);
    }

    private Object calcDyn1(int num, String str1) {
        int randomVal = random.nextInt(100);
        if (randomVal < 95)
            return num;
        return str1;
    }

    private Object calcDyn2(int num, String str1, boolean currentBool) {
        int randomVal = random.nextInt(3);
        switch (randomVal) {
            case 0:
                return num;
            case 1:
                return str1;
            case 2:
            default:
                return currentBool;
        }
    }

    private JsonNode insertSparseAttr(NoBenchEntity entity, List<Cluster> clusters) {
        JsonNode jsonNode = new ObjectMapper().valueToTree(entity);
        Cluster cluster = clusters.get(random.nextInt(clusters.size()));
        for (int i = 0; i < 10; i++) {
            String fieldName = String.format("%s%02d%s", "sparse", cluster.getIndex() * 10, i);
            ((ObjectNode) jsonNode).put(fieldName, cluster.getData().get(i));
        }
        return jsonNode;
    }

    @Value
    private static class NoBenchEntity {
        String str1, str2;
        int num;
        boolean bool;
        Object dyn1, dyn2;
        String[] nested_arr;
        NestedObj nested_obj;
        int thousandth;
    }

    @Value
    private static class NestedObj {
        String str;
        int num;
    }

    @Value
    private static class StrCache {
        String str1, str2;
    }

    @Value
    private static class Cluster {
        int index;
        List<String> data;
    }
}
