package markmixson.prioritysort;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

public class RedisPrioritySortClientTestData {
    public static final String FIXED_DATE = "2023-05-09T01:12:25Z";
    public static final Clock CLOCK = Clock.fixed(Instant.parse(FIXED_DATE), ZoneId.of("UTC"));
    public static final int BITSET_LENGTH = 64;
    public static final BitSetGenerator GENERATOR = new BitSetGenerator();
    public static final Random RANDOM = new Random();

    /**
     * All Fibonacci numbers below 64.
     */
    public static final RuleMatchResults FIRST = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(1356357L)
            .build();

    /**
     * Minus 55.
     */
    public static final RuleMatchResults SECOND = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 8, 13, 21, 34}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(5367367L)
            .build();

    /**
     * Minus 8.
     */
    public static final RuleMatchResults THIRD = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1, 2, 3, 5, 13, 21, 34, 55}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(4356356L)
            .build();

    /**
     * Minus 0.
     */
    public static final RuleMatchResults FOURTH = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(-12314L)
            .build();

    /**
     * Minus 0 and 10 seconds later.
     */
    public static final RuleMatchResults FIFTH = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34, 55}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant().plus(Duration.ofSeconds(10)), CLOCK.getZone()))
            .id(33573573567356356L)
            .build();

    /**
     * Minus 0 and 55.
     */
    public static final RuleMatchResults SIXTH = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{1, 2, 3, 5, 8, 13, 21, 34}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(0L)
            .build();

    /**
     * Updated first to have lowest results
     */
    public static final RuleMatchResults UPDATED_FIRST = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0}, BITSET_LENGTH))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(1356357L)
            .build();

    /**
     * 1 match
     */
    public static final RuleMatchResults ONE_MATCH = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0}, 1))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(1L)
            .build();

    /**
     * 0 matches
     */
    public static final RuleMatchResults ZERO_MATCHES = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{}, 1))
            .date(ZonedDateTime.ofInstant(CLOCK.instant().minus(Duration.ofSeconds(30)), CLOCK.getZone()))
            .id(0L)
            .build();

    /**
     * 0 matches but later.
     */
    public static final RuleMatchResults ZERO_MATCHES_EARLIER = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{}, 1))
            .date(ZonedDateTime.ofInstant(CLOCK.instant().minus(Duration.ofDays(30)), CLOCK.getZone()))
            .id(-1L)
            .build();

    /**
     * 0 matches no length.
     */
    public static final RuleMatchResults ZERO_MATCHES_NO_LENGTH = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{}, 0))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(-99999L)
            .build();

    /**
     * 1 match, 1 didn't
     */
    public static final RuleMatchResults ONE_MATCH_ONE_DID_NOT = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{1}, 2))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(113413532L)
            .build();

    /**
     * 2 matches
     */
    public static final RuleMatchResults TWO_MATCHES = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1}, 2))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(1234234L)
            .build();

    /**
     * 7 out of 8 matches
     */
    public static final RuleMatchResults SEVEN_OUT_OF_EIGHT_MATCHES = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1, 2, 3, 4, 5, 6}, 8))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(-113413532L)
            .build();

    /**
     * 8 matches
     */
    public static final RuleMatchResults EIGHT_MATCHES = RuleMatchResults.builder()
            .matched(GENERATOR.generate(new int[]{0, 1, 2, 3, 4, 5, 6, 7}, 8))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(-1234234L)
            .build();

    /**
     * List of match results in expected order.
     */
    public static final List<RuleMatchResults> RULE_MATCH_RESULTS = List.of(FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH);
}
