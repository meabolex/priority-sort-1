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
        StepVerifier.create(getClient().mutation().clear(QUERY_SUFFIX))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetPriorities() {
        final var results = RULE_MATCH_RESULTS.stream()
                .map(RuleMatchResults::id)
                .toList();
        StepVerifier.create(getClient().query().getTopPriorities(QUERY_SUFFIX, results.size()).collectList())
                .expectNext(results)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriority() {
        StepVerifier.create(getClient().query().getIndexCount(QUERY_SUFFIX))
                .expectNext((long) RULE_MATCH_RESULTS.size())
                .expectComplete()
                .verify();
        StepVerifier.create(getClient().query().getTopPriority(QUERY_SUFFIX))
                .expectNext(FIRST.id())
                .expectComplete()
                .verify();
    }

    @Test
    void testGetHighestPriorityRuleMatchResults() {
        StepVerifier.create(getClient().query().getTopPriorityRuleMatchResult(QUERY_SUFFIX))
                .expectNext(FIRST)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetRuleMatchResults() {
        StepVerifier.create(getClient().query().getRuleMatchResults(QUERY_SUFFIX, FIFTH.id()))
                .expectNext(FIFTH)
                .expectComplete()
                .verify();
    }

    @Test
    void testAllGetRuleMatchResults() {
        StepVerifier.create(getClient().query().getAllRuleMatchResults(QUERY_SUFFIX).collectList())
                .expectNextMatches(result -> result.containsAll(RULE_MATCH_RESULTS))
                .expectComplete()
                .verify();
    }
}
