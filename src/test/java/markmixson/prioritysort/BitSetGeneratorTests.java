package markmixson.prioritysort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BitSetGeneratorTests {

    @Test
    void testGeneratorValueBiggerThanLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new BitSetGenerator().generate(new int[] { 100 }, 99));
    }
}