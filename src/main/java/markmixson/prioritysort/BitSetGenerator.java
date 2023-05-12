package markmixson.prioritysort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Stream;

/**
 * Gets a {@link BitSet} that can be used inside {@link RuleMatchResults}.
 */
public class BitSetGenerator {

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private BitSet allBits;

    /**
     * How to generate a correct {@link BitSet}.
     * <p>
     * 1. Length must be larger than the largest value in values.
     * 2. The BitSet size must be the next integer larger than length but divisible by 8.
     * 3. All bits must be set initially.
     * 4. Flip each bit (off) for each given rule.
     *
     * @param values values to flip (lowest value is highest priority rule).
     * @param length the requested length.
     * @return the bitset
     */
    public BitSet generate(final int @NonNull [] values, final int length) {
        if (Arrays.stream(values).anyMatch(value -> value > length)) {
            throw new IllegalArgumentException("Invalid bitset length!  Must be greater than biggest value");
        }
        final var bitSetSize = length + Byte.SIZE - length % Byte.SIZE;
        final var bitSet = getAllTrueBitSet(bitSetSize);
        Arrays.stream(values)
                .forEach(bitSet::flip);
        return bitSet;
    }

    private BitSet getAllTrueBitSet(final int length) {
        return Stream.ofNullable(getAllBits())
                .filter(mySet -> mySet.length() == length)
                .map(mySet -> (BitSet) mySet.clone())
                .findFirst()
                .orElseGet(() -> {
                    setAllBits(new BitSet(length));
                    getAllBits().flip(0, length - 1);
                    return (BitSet) getAllBits().clone();
                });
    }
}
