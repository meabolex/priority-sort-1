package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.ONE_MATCH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RULE_MATCH_RESULTS;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SECOND;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.UPDATED_FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.ZERO_MATCHES;

public class RedisPrioritySortMutationClientTests extends RedisPrioritySortClientTests {
    private static final String MUTATION_SUFFIX = "mutation";

    @AfterEach
    void cleanUp() {
        StepVerifier.create(getClient().getMutation().clear(MUTATION_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSingleRule() {
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, ONE_MATCH))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, ZERO_MATCHES))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                .expectNext(ONE_MATCH.id())
                .expectComplete()
                .verify();
    }

    @Nested
    protected class WithBeforeEach {

        @BeforeEach
        void setUp() {
            doAddOrUpdateTestData(MUTATION_SUFFIX);
        }

        @Test
        void testClear() {
            StepVerifier.create(getClient().getMutation().clear(MUTATION_SUFFIX))
                    .expectNextMatches(result -> result > 0)
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                    .expectNextCount(0)
                    .expectComplete()
                    .verify();
        }

        @Test
        void testDelete() {
            StepVerifier.create(getClient().getMutation().delete(MUTATION_SUFFIX, FIRST.id()))
                    .expectNextMatches(result -> result == 1L)
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                    .expectNext(SECOND.id())
                    .expectComplete()
                    .verify();
        }

        @Test
        void testUpdate() {
            StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                    .expectNext(FIRST.id())
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, UPDATED_FIRST))
                    .expectNext(1L)
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                    .expectNext(SECOND.id())
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getMutation().delete(MUTATION_SUFFIX, FIRST.id()))
                    .expectNextMatches(result -> result == 1L)
                    .expectComplete()
                    .verify();
            StepVerifier.create(getClient().getQuery().getIndexCount(MUTATION_SUFFIX))
                    .expectNext((long) RULE_MATCH_RESULTS.size() - 1)
                    .expectComplete()
                    .verify();
        }
    }
}
