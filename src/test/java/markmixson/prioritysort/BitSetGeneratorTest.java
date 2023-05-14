package markmixson.prioritysort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.stream.IntStream;

public class BitSetGeneratorTest {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private BitSetGenerator generator;

    @BeforeEach
    void setUp() {
        setGenerator(new BitSetGenerator());
    }

    @Test
    void testGeneratorValueBiggerThanLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{100}, 99));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testGeneratorNullValues() {
        Assertions.assertThrows(NullPointerException.class, () ->
                getGenerator().generate(null, 99));
    }

    @Test
    void testGeneratorLengthLessThanSizeOfValues() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{0, 1}, 1));
    }

    @Test
    void testGeneratorLengthLessThanZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getGenerator().generate(new int[]{}, -1));
    }

    @Test
    void testGeneratorEmpty() {
        Assertions.assertEquals(new BitSet(0), getGenerator().generate(new int[] {}, 0));
    }

    @Test
    void testGeneratorChangeLengths() {
        testGenerator(IntStream.range(0, 7).toArray(), 8, 1);
        testGenerator(IntStream.range(1, 7).toArray(), 8, 2);
        testGenerator(IntStream.range(1, 7).toArray(), 16, 10);
        testGenerator(IntStream.range(1, 7).toArray(), 12, 10);
    }

    private void testGenerator(final int[] values, final int length, final int cardinality) {
        final var result = getGenerator().generate(values, length);
        Assertions.assertEquals(cardinality, result.cardinality());
    }
}