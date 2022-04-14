package ktu.masters.generate;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NoBenchDataGeneratorTest {
    private final NoBenchDataGenerator generator = new NoBenchDataGenerator();

    @Test
    void testSave() {
        assertDoesNotThrow(() -> generator.generateAndSave(10, "NO_BENCH_TEST"));
    }

    @Test
    void testOne() {
        List<JsonNode> actual = generator.generate(1);

        assertThat(actual).hasSize(1);
    }

    @Test
    void testOneThousand() {
        List<JsonNode> actual = generator.generate(1000);

        assertThat(actual).hasSize(1000);
        assertThat(actual)
                .extracting(obj -> obj.get("bool").asBoolean())
                .filteredOn(TRUE::equals)
                .hasSizeBetween(490, 510);
        assertThat(actual)
                .noneMatch(v -> v.get("str1").equals(v.get("str2")));
    }

    @Test
    @Timeout(value = 30)
    void testOneMillion() {
        List<JsonNode> actual = generator.generate(1000_000);

        assertThat(actual).hasSize(1000000);
    }
}
