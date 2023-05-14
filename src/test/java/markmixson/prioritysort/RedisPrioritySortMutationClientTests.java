package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.*;

public class RedisPrioritySortMutationClientTests extends RedisPrioritySortClientTests {
    private static final String MUTATION_SUFFIX = "mutation";

    @AfterEach
    void cleanUp() {
        getClient().getMutation().clear(MUTATION_SUFFIX).block();
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
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, ZERO_MATCHES_EARLIER))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriorities(MUTATION_SUFFIX, 3))
                .expectNext(ONE_MATCH.id())
                .expectNext(ZERO_MATCHES_EARLIER.id())
                .expectNext(ZERO_MATCHES.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testNoRules() {
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, ZERO_MATCHES_NO_LENGTH))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                .expectNext(ZERO_MATCHES_NO_LENGTH.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testTwoMatches() {
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, TWO_MATCHES))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, ONE_MATCH_ONE_DID_NOT))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                .expectNext(TWO_MATCHES.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testEightMatchesOneNone() {
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, EIGHT_MATCHES))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, SEVEN_OUT_OF_EIGHT_MATCHES))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriority(MUTATION_SUFFIX))
                .expectNext(EIGHT_MATCHES.id())
                .expectComplete()
                .verify();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testNullMatchesAdd() {
        Assertions.assertThrows(NullPointerException.class,
                () -> getClient().getMutation().addOrUpdate(MUTATION_SUFFIX, null).block());
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
