package markmixson.prioritysort;

import com.google.common.primitives.Bytes;
import lombok.NonNull;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.BitSet;

/**
 * Record representing rule match results.
 */
public record RuleMatchResults(@NonNull BitSet matched, @NonNull ZonedDateTime date, @NonNull Long id) {

    /**
     * Converts the record into a big-endian byte format.
     *
     * @return a {@link byte[]} representing the {@link RuleMatchResults}.
     */
    public byte[] toByteArray() {
        final var buffer = ByteBuffer.allocate(Long.BYTES * 2);
        buffer.putLong(date().toEpochSecond());
        buffer.putLong(id());
        return Bytes.concat(matched().toByteArray(), buffer.array());
    }

    /**
     * Given a {@link byte[]}, get a {@link RuleMatchResults} back.
     *
     * @param bytes the bytes to convert.
     * @return the {@link RuleMatchResults}.
     */
    public static RuleMatchResults getRuleMatchResults(final byte @NonNull [] bytes) {
        if (bytes.length == 0) {
            throw new IllegalArgumentException("byte array cannot be empty!");
        }
        return getRuleMatchResults(ByteBuffer.wrap(bytes));
    }

    private static RuleMatchResults getRuleMatchResults(final ByteBuffer input) {
        final var matchedSize = input.array().length - (Long.BYTES * 2);
        final var matchedSlice = input.slice(0, matchedSize);
        final var dateSlice = input.slice(matchedSize, Long.BYTES);
        final var idSlice = input.slice(matchedSize + Long.BYTES, Long.BYTES);
        return new RuleMatchResults(
                BitSet.valueOf(matchedSlice),
                ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(dateSlice.getLong()), ZoneId.of("UTC")),
                idSlice.getLong());
    }
}
