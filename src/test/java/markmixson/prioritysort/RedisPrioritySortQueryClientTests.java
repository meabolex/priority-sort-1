package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RULE_MATCH_RESULTS;

public class RedisPrioritySortQueryClientTests extends RedisPrioritySortClientTests {
    private static final String QUERY_SUFFIX = "query";

    @AfterEach
    void cleanUp() {
        StepVerifier.create(getMutationClient().clear(QUERY_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetPriorities() {
        final var results = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        doAddOrUpdateTestData(QUERY_SUFFIX, getMutationClient());
        StepVerifier.create(getQueryClient().getTopPriorities(QUERY_SUFFIX, results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
        doAddOrUpdateTestData(QUERY_SUFFIX, getMutationClient());
        StepVerifier.create(getQueryClient().getIndexCount(QUERY_SUFFIX))
                .expectNext((long) RULE_MATCH_RESULTS.size())
                .expectComplete()
                .verify();
        StepVerifier.create(getQueryClient().getTopPriority(QUERY_SUFFIX))
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriorityRuleMatchResults() {
        doAddOrUpdateTestData(QUERY_SUFFIX, getMutationClient());
        StepVerifier.create(getQueryClient().getTopPriorityRuleMatchResult(QUERY_SUFFIX))
                .expectNext(FIRST)
                .expectComplete()
                .verify();
    }
}
