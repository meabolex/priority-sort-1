package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIFTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RULE_MATCH_RESULTS;

public class RedisPrioritySortQueryClientTests extends RedisPrioritySortClientTests {
    private static final String QUERY_SUFFIX = "query";

    @BeforeEach
    void setUp() {
        doAddOrUpdateTestData(QUERY_SUFFIX, getMutationClient());
    }

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
        StepVerifier.create(getQueryClient().getTopPriorities(QUERY_SUFFIX, results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
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
        StepVerifier.create(getQueryClient().getTopPriorityRuleMatchResult(QUERY_SUFFIX))
                .expectNext(FIRST)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetRuleMatchResults() {
        StepVerifier.create(getQueryClient().getRuleMatchResults(QUERY_SUFFIX, FIFTH.id()))
                .expectNext(FIFTH)
                .expectComplete()
                .verify();
    }

    @Test
    void testAllGetRuleMatchResults() {
        StepVerifier.create(getQueryClient().getAllRuleMatchResults(QUERY_SUFFIX).collectList())
                .expectNextMatches(result -> result.containsAll(RULE_MATCH_RESULTS))
                .expectComplete()
                .verify();
    }
}
