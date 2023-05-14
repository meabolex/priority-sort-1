package markmixson.prioritysort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class BitSetGeneratorTests {

    @Test
    void testGeneratorValueBiggerThanLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new BitSetGenerator().generate(new int[]{100}, 99));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testGeneratorNullValues() {
        Assertions.assertThrows(NullPointerException.class, () ->
                new BitSetGenerator().generate(null, 99));
    }

    @Test
    void testGeneratorChangeLengths() {
        final var generator = new BitSetGenerator();
        final var first = generator.generate(IntStream.range(0, 7).toArray(), 8);
        Assertions.assertEquals(1, first.cardinality());
        final var second = generator.generate(IntStream.range(1, 7).toArray(), 8);
        Assertions.assertEquals(2, second.cardinality());
        final var third = generator.generate(IntStream.range(1, 7).toArray(), 16);
        Assertions.assertEquals(10, third.cardinality());
    }
}