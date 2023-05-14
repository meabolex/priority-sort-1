package markmixson.prioritysort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIFTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.RULE_MATCH_RESULTS;

public class RedisPrioritySortQueryClientTest extends RedisPrioritySortClientTest {
    private static final String QUERY_SUFFIX = "query";

    @BeforeEach
    void setUp() {
        doAddOrUpdateTestData(QUERY_SUFFIX);
    }

    @AfterEach
    void cleanUp() {
        StepVerifier.create(getClients().getMutation().clear(QUERY_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetPriorities() {
        final var results = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        StepVerifier.create(getClients().getQuery().getTopPriorities(QUERY_SUFFIX, results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
        StepVerifier.create(getClients().getQuery().getIndexCount(QUERY_SUFFIX))
                .expectNext((long) RULE_MATCH_RESULTS.size())
                .expectComplete()
                .verify();
        StepVerifier.create(getClients().getQuery().getTopPriority(QUERY_SUFFIX))
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriorityRuleMatchResults() {
        StepVerifier.create(getClients().getQuery().getTopPriorityRuleMatchResult(QUERY_SUFFIX))
                .expectNext(FIRST)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetRuleMatchResults() {
        StepVerifier.create(getClients().getQuery().getRuleMatchResults(QUERY_SUFFIX, FIFTH.id()))
                .expectNext(FIFTH)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllRuleMatchResults() {
        StepVerifier.create(getClients().getQuery().getTopPriorityRuleMatchResults(QUERY_SUFFIX, -1).collectList())
                .expectNextMatches(result -> result.containsAll(RULE_MATCH_RESULTS))
                .expectComplete()
                .verify();
    }

    @Test
    void testAllPriorities() {
        final var priorities = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        StepVerifier.create(getClients().getQuery().getTopPriorities(QUERY_SUFFIX, -1).collectList())
                .expectNextMatches(result -> result.containsAll(priorities))
                .expectComplete()
                .verify();
    }
}
