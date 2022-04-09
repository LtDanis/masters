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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class NoBenchDataGenerator {
    private final Set<String> usedStr1;
    private final AtomicBoolean bool;
    private final Random random;

    public NoBenchDataGenerator() {
        this.usedStr1 = new ConcurrentSkipListSet<>();
        this.bool = new AtomicBoolean(true);
        this.random = new Random(System.nanoTime());
    }

    public void generateAndSave(long numOfItems, String fileName) {
        List<JsonNode> items = generate(numOfItems);
        try (OutputStreamWriter file = new FileWriter(String.format("./src/main/resources/%s.json", fileName))) {
            file.write(JSONArray.toJSONString(items));
        } catch (IOException e) {
            throw new ApiException(500, e, "Failed to save generated NO_BENCH data");
        }
    }

    public List<JsonNode> generate(long numOfItems) {
        return LongStream.range(0L, numOfItems).parallel()
                .mapToObj(this::createEntity)
                .map(this::insertSparseAttr)
                .collect(toList());
    }

    private NoBenchEntity createEntity(long index) {
        String str1 = UUID.randomUUID().toString();
        String str2 = UUID.randomUUID().toString();
        boolean currentBool = bool.getAndSet(!bool.get());
        return new NoBenchEntity(str1,
                str2,
                index,
                currentBool,
                calcDyn1(index, str1),
                calcDyn2(index, str1, currentBool),
                createArr(),
                calcNested(str1, index),
                (int) index % 1000
        );
    }

    private String[] createArr() {
        return new String[0]; //TODO
    }

    private NestedObj calcNested(String str1, long num) {
        long n = random.nextInt(usedStr1.size() + 1);
        String nestedStr = usedStr1.stream()
                .skip(n)
                .findFirst()
                .orElse(UUID.randomUUID().toString());
        usedStr1.add(str1);
        long nestedNum = num == n ? num + 1 : n;
        return new NestedObj(nestedStr, nestedNum);
    }

    private Object calcDyn1(long num, String str1) {
        int randomVal = random.nextInt(100);
        if (randomVal < 95)
            return num;
        return str1;
    }

    private Object calcDyn2(long num, String str1, boolean currentBool) {
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

    private JsonNode insertSparseAttr(NoBenchEntity entity) {
        JsonNode jsonNode = new ObjectMapper().valueToTree(entity);
        ((ObjectNode) jsonNode).put("sparseXXX", "XXX");//TODO
        return jsonNode;
    }

    @Value
    private static class NoBenchEntity {
        String str1, str2;
        long num;
        boolean bool;
        Object dyn1, dyn2;
        String[] nested_arr;
        NestedObj nested_obj;
        int thousandth;
    }

    @Value
    private static class NestedObj {
        String str;
        long num;
    }
}
