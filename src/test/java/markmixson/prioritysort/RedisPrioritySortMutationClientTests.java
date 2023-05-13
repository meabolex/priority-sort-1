package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.ONE_MATCH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.ZERO_MATCHES;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SECOND;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.UPDATED_FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RULE_MATCH_RESULTS;

public class RedisPrioritySortMutationClientTests extends RedisPrioritySortClientTests {
    private static final String MUTATION_SUFFIX = "mutation";

    @AfterEach
    void cleanUp() {
        StepVerifier.create(getMutationClient().clear(MUTATION_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSingleRule() {
        StepVerifier.create(getMutationClient().addOrUpdate(MUTATION_SUFFIX, ONE_MATCH))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getMutationClient().addOrUpdate(MUTATION_SUFFIX, ZERO_MATCHES))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getTopPriority(MUTATION_SUFFIX))
                .expectNext(ONE_MATCH.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testClear() {
        doAddOrUpdateTestData(MUTATION_SUFFIX, getMutationClient());
        StepVerifier.create(getMutationClient().clear(MUTATION_SUFFIX))
                .expectNextMatches(result -> result > 0)
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getTopPriority(MUTATION_SUFFIX))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void testDelete() {
        doAddOrUpdateTestData(MUTATION_SUFFIX, getMutationClient());
        StepVerifier.create(getMutationClient().delete(MUTATION_SUFFIX, FIRST.id()))
                .expectNextMatches(result -> result == 1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getTopPriority(MUTATION_SUFFIX))
                .expectNext(SECOND.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {
        doAddOrUpdateTestData(MUTATION_SUFFIX, getMutationClient());
        StepVerifier.create(getQueryClient().getTopPriority(MUTATION_SUFFIX))
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
        StepVerifier.create(getMutationClient().addOrUpdate(MUTATION_SUFFIX, UPDATED_FIRST))
                .expectNext(1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getTopPriority(MUTATION_SUFFIX))
                .expectNext(SECOND.id())
                .expectComplete()
                .verify();
        StepVerifier.create(getMutationClient().delete(MUTATION_SUFFIX, FIRST.id()))
                .expectNextMatches(result -> result == 1L)
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getIndexCount(MUTATION_SUFFIX))
                .expectNext((long) RULE_MATCH_RESULTS.size() - 1)
                .expectComplete()
                .verify();
    }
}
