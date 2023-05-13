package markmixson.prioritysort;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.CLOCK;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.GENERATOR;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RANDOM;

public class RedisPrioritySortMutationClientLargeAddTest extends RedisPrioritySortClientTests {

    /**
     * Increasing this value can cause problems with Redis.
     */
    private static final int PARALLEL_PROCESSES = Runtime.getRuntime().availableProcessors() / 2;
    private static final int LARGE_RULE_COUNT = 203;
    private static final int LARGE_DATA_COUNT = 1_500_000;
    private static final String LARGE_ADD_SUFFIX = "largeadd";
    private static final int[] ALL_SELECTED = IntStream.range(0, LARGE_RULE_COUNT).toArray();
    private static final RuleMatchResults HIGHEST_POSSIBLE = RuleMatchResults.builder()
            .matched(GENERATOR.generate(ALL_SELECTED, LARGE_RULE_COUNT))
            .date(ZonedDateTime.ofInstant(CLOCK.instant(), CLOCK.getZone()))
            .id(Long.MAX_VALUE)
            .build();

    @Test
    @EnabledIfEnvironmentVariable(named = "RUN_BIG_TESTS", matches = "true")
    void testLargeNumberOfAdds() {
        StepVerifier.create(getClient().getMutation().addOrUpdate(LARGE_ADD_SUFFIX, HIGHEST_POSSIBLE))
                .expectNext(1L)
                .expectComplete()
                .verify();
        doLargeNumberOfAdds();
        StepVerifier.create(getClient().getQuery().getTopPriority(LARGE_ADD_SUFFIX))
                .expectNext(Long.MAX_VALUE)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getIndexCount(LARGE_ADD_SUFFIX))
                .expectNext((long) (LARGE_DATA_COUNT + 1))
                .expectComplete()
                .verify();
    }

    @SneakyThrows(InterruptedException.class)
    private void doLargeNumberOfAdds() {
        final var results = getRandomIds().stream()
                .<Callable<Void>>map(id -> () -> {
                    getClient().getMutation().addOrUpdate(LARGE_ADD_SUFFIX, getRandomRuleMatchResults(id)).block();
                    return null;
                })
                .toList();
        try (var executor = Executors.newWorkStealingPool(PARALLEL_PROCESSES)) {
            final var output = executor.invokeAll(results);
            Assertions.assertEquals(LARGE_DATA_COUNT, output.size());
        }
    }

    private RuleMatchResults getRandomRuleMatchResults(@NonNull final Long id) {
        final var epochSecond = RANDOM.nextLong(CLOCK.instant().getEpochSecond());
        return RuleMatchResults.builder()
                .matched(GENERATOR.generate(getRandomMatches(), LARGE_RULE_COUNT))
                .date(ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("UTC")))
                .id(id)
                .build();
    }

    private int[] getRandomMatches() {
        final var matches = new ArrayList<Integer>();
        IntStream.range(0, LARGE_RULE_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        randomizeMatches(matches);
        return matches.stream()
                .mapToInt(x -> x)
                .toArray();
    }

    private void randomizeMatches(@NonNull final ArrayList<Integer> matches) {
        for (var i = matches.size() - 1; i >= 0; i--) {
            if (!RANDOM.nextBoolean()) {
                matches.remove(i);
            }
        }
    }

    private List<Long> getRandomIds() {
        final var matches = new ArrayList<Long>();
        LongStream.range(0, LARGE_DATA_COUNT).forEach(matches::add);
        Collections.shuffle(matches, RANDOM);
        return matches;
    }
}
