package markmixson.prioritysort;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import reactor.test.StepVerifier;

@SpringBootTest
@Getter(AccessLevel.PRIVATE)
@ContextConfiguration(initializers = RedisInitializer.class)
public class PrioritySortClientTests {
    private static final int PARALLEL_PROCESSES = Runtime.getRuntime().availableProcessors() / 2;
    private static final BitSetGenerator GENERATOR = new BitSetGenerator();
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2023-05-09T01:12:25Z"), ZoneId.of("UTC"));
    private static final int BITSET_LENGTH = 64;
    private static final int LARGE_RULE_COUNT = 203;
    private static final int LARGE_DATA_COUNT = 1_500_000;
    private static final Random RANDOM = new Random();

    /**
     * All Fibonacci numbers below 64.
     */
    private static final RuleMatchResults FIRST = new RuleMatchResults(
            GENERATOR.generate(new int[] { 0, 1, 2, 3, 5, 8, 13, 21, 34, 55 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1356357L);

    /**
     * Minus 55.
     */
    private static final RuleMatchResults SECOND = new RuleMatchResults(
            GENERATOR.generate(new int[] { 0, 1, 2, 3, 5, 8, 13, 21, 34 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            5367367L);

    /**
     * Minus 8.
     */
    private static final RuleMatchResults THIRD = new RuleMatchResults(
            GENERATOR.generate(new int[] { 0, 1, 2, 3, 5, 13, 21, 34, 55 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            4356356L);

    /**
     * Minus 0.
     */
    private static final RuleMatchResults FOURTH = new RuleMatchResults(
            GENERATOR.generate(new int[] { 1, 2, 3, 5, 8, 13, 21, 34, 55 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            -12314L);

    /**
     * Minus 0 and 10 seconds later.
     */
    private static final RuleMatchResults FIFTH = new RuleMatchResults(
            GENERATOR.generate(new int[] { 1, 2, 3, 5, 8, 13, 21, 34, 55 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant().plus(Duration.ofSeconds(10)), CLOCK.getZone()),
            33573573567356356L);

    /**
     * Minus 0 and 55.
     */
    private static final RuleMatchResults SIXTH = new RuleMatchResults(
            GENERATOR.generate(new int[] { 1, 2, 3, 5, 8, 13, 21, 34 }, BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            0L);

    /**
     * Updated first to have lowest results
     */
    private static final RuleMatchResults UPDATED_FIRST = new RuleMatchResults(
            GENERATOR.generate(new int[] { 0 },
                    BITSET_LENGTH),
            ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
            1356357L);


    private static final List<RuleMatchResults> RULE_MATCH_RESULTS = List.of(FIRST, SECOND, THIRD, FOURTH, FIFTH,
            SIXTH);
    private static final List<RuleMatchResults> RULE_MATCH_RESULTS_SCRAMBLED = List.of(THIRD, FOURTH, FIRST, FIFTH,
            SIXTH, SECOND);

    @Autowired
    private RedisPrioritySortClient prioritySortClient;

    @AfterEach
    void cleanUp() {
        getPrioritySortClient().clear().block();
    }

    @Test
    void testGetPriorities() {
        final var results = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .collect(Collectors.toList());
        doAddOrUpdateTestData();
        StepVerifier.create(getPrioritySortClient().getTopPriorities(results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {
        doAddOrUpdateTestData();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().addOrUpdate(UPDATED_FIRST))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNext(SECOND.id())
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().delete(FIRST.id()))
                .expectNextMatches(result -> result == 1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getIndexCount())
                .expectNext((long) RULE_MATCH_RESULTS.size() - 1)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
        doAddOrUpdateTestData();
        StepVerifier.create(getPrioritySortClient().getIndexCount())
                .expectNext((long) RULE_MATCH_RESULTS.size())
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testClear() {
        doAddOrUpdateTestData();
        StepVerifier.create(getPrioritySortClient().clear())
                .expectNextMatches(result -> result > 0)
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void testDelete() {
        doAddOrUpdateTestData();
        StepVerifier.create(getPrioritySortClient().delete(FIRST.id()))
                .expectNextMatches(result -> result == 1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNext(SECOND.id())
                .expectComplete()
                .verify();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BIG_TESTS", matches = "true")
    void testLargeNumberOfAdds() {
        StepVerifier.create(getPrioritySortClient().addOrUpdate(getHighestPriorityPossible()))
                .expectNext(1L)
                .expectComplete()
                .verify();
        doLargeNumberOfAdds();
        StepVerifier.create(getPrioritySortClient().getTopPriority())
                .expectNext(Long.MAX_VALUE)
                .expectComplete()
                .verify();
        StepVerifier.create(getPrioritySortClient().getIndexCount())
                .expectNext((long) (LARGE_DATA_COUNT + 1))
                .expectComplete()
                .verify();
    }

    @SneakyThrows
    private void doLargeNumberOfAdds() {
        final var results = getRandomIds().stream()
                .<Callable<Long>>map(
                        id -> () -> getPrioritySortClient().addOrUpdate(getRandomRuleMatchResults(id)).block())
                .collect(Collectors.toList());
        try (var executor = Executors.newWorkStealingPool(PARALLEL_PROCESSES)) {
            final var output = executor.invokeAll(results);
            Assertions.assertEquals(LARGE_DATA_COUNT, output.size());
        }
    }

    @SneakyThrows
    private void doAddOrUpdateTestData() {
        final var results = RULE_MATCH_RESULTS_SCRAMBLED.stream()
                .<Callable<Long>>map(result -> () -> getPrioritySortClient().addOrUpdate(result).block())
                .collect(Collectors.toList());
        try (var executor = Executors.newWorkStealingPool(PARALLEL_PROCESSES)) {
            final var output = executor.invokeAll(results);
            Assertions.assertEquals(RULE_MATCH_RESULTS_SCRAMBLED.size(), output.size());
        }
    }

    private RuleMatchResults getRandomRuleMatchResults(final Long id) {
        final var matches = GENERATOR.generate(getRandomMatches(), LARGE_RULE_COUNT);
        final var epochSecond = RANDOM.nextLong(CLOCK.instant().getEpochSecond());
        final var date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("UTC"));
        return new RuleMatchResults(matches, date, id);
    }

    private int[] getRandomMatches() {
        final var matches = new ArrayList<Integer>();
        IntStream.range(0, LARGE_RULE_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        for (var i = matches.size() - 1; i >= 0; i--) {
            if (!RANDOM.nextBoolean()) {
                matches.remove(i);
            }
        }
        return matches.stream()
                .mapToInt(x -> x)
                .toArray();
    }

    private List<Long> getRandomIds() {
        final var matches = new ArrayList<Long>();
        LongStream.range(0, LARGE_DATA_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        return matches;
    }

    private RuleMatchResults getHighestPriorityPossible() {
        final var allMatches = IntStream.range(0, LARGE_RULE_COUNT).toArray();
        return new RuleMatchResults(
                GENERATOR.generate(allMatches, LARGE_RULE_COUNT),
                ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()),
                Long.MAX_VALUE);
    }
}
