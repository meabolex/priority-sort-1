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
        doAddOrUpdateTestData(QUERY_SUFFIX);
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(getClient().getMutation().clear(QUERY_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetPriorities() {
        final var results = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        StepVerifier.create(getClient().getQuery().getTopPriorities(QUERY_SUFFIX, results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
        StepVerifier.create(getClient().getQuery().getIndexCount(QUERY_SUFFIX))
                .expectNext((long) RULE_MATCH_RESULTS.size())
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().getQuery().getTopPriority(QUERY_SUFFIX))
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriorityRuleMatchResults() {
        StepVerifier.create(getClient().getQuery().getTopPriorityRuleMatchResult(QUERY_SUFFIX))
                .expectNext(FIRST)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetRuleMatchResults() {
        StepVerifier.create(getClient().getQuery().getRuleMatchResults(QUERY_SUFFIX, FIFTH.id()))
                .expectNext(FIFTH)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllRuleMatchResults() {
        StepVerifier.create(getClient().getQuery().getTopPriorityRuleMatchResults(QUERY_SUFFIX, -1).collectList())
                .expectNextMatches(result -> result.containsAll(RULE_MATCH_RESULTS))
                .expectComplete()
                .verify();
    }

    @Test
    void testAllPriorities() {
        final var priorities = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        StepVerifier.create(getClient().getQuery().getTopPriorities(QUERY_SUFFIX, -1).collectList())
                .expectNextMatches(result -> result.containsAll(priorities))
                .expectComplete()
                .verify();
    }
}
