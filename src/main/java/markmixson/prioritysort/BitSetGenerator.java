package markmixson.prioritysort;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Stream;

/**
 * Gets a {@link BitSet} that can be used inside {@link RuleMatchResults}.
 */
@NoArgsConstructor
public class BitSetGenerator {

    /**
     * Cached {@link BitSet} used when requesting same cardinality repeatedly.
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private BitSet cachedBitSet;

    /**
     * How to generate a correct {@link BitSet}.
     * <p>
     * 1. Length must be larger than the largest value in values.<p>
     * 2. The BitSet size must be the next integer larger than length but divisible by 8.<p>
     * 3. All bits must be set initially.<p>
     * 4. Flip each bit for each given rule.
     *
     * @param values values to flip (lowest value is highest priority rule).
     * @param length the requested length.
     * @return the bitset
     */
    public BitSet generate(final int @NonNull [] values, final int length) {
        Preconditions.checkArgument(values.length <= length);
        if (length == 0) {
            return new BitSet(0);
        }
        final var range = Range.between(0, length - 1);
        Preconditions.checkArgument(Arrays.stream(values).allMatch(range::contains));
        final var allBitsTrueSize = getLengthRoundedUpToNearestByte(length);
        final var bitSet = getFlippedBitSet(allBitsTrueSize);
        Arrays.stream(values).forEach(bitSet::flip);
        return bitSet;
    }

    /**
     * Will hold/reuse the cached {@link BitSet} for all true until the cardinality changes.
     *
     * @param allSetCardinality the cardinality of the bitset requested.
     * @return the bitset.
     */
    private BitSet getFlippedBitSet(final int allSetCardinality) {
        return (BitSet) Stream.ofNullable(getCachedBitSet())
                .filter(mySet -> mySet.cardinality() == allSetCardinality)
                .map(BitSet::clone)
                .findFirst()
                .orElseGet(() -> {
                    setCachedBitSet(new BitSet(allSetCardinality));
                    getCachedBitSet().flip(0, allSetCardinality);
                    return getCachedBitSet().clone();
                });
    }

    private int getLengthRoundedUpToNearestByte(final int length) {
        final int mod = length % Byte.SIZE;
        if (mod == 0) {
            return length;
        } else {
            return length + Byte.SIZE - mod;
        }
    }
}
