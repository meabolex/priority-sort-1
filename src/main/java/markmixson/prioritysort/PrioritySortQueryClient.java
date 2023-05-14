package markmixson.prioritysort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Query client for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortQueryClient {

    /**
     * Gets the top priority id from the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    Mono<Long> getTopPriority(String keySuffix);

    /**
     * Gets the top N priorities ids from the index in order.
     * A negative count returns all ids in priority order.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param count     the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    Flux<Long> getTopPriorities(String keySuffix, long count);

    /**
     * Gets the top priority id from the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    Mono<RuleMatchResults> getTopPriorityRuleMatchResult(String keySuffix);

    /**
     * Gets the top N priorities ids from the index in order.
     * A negative count returns all priorities in order.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param count     the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    Flux<RuleMatchResults> getTopPriorityRuleMatchResults(String keySuffix, long count);

    /**
     * Gets the overall count of elements in the index.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the count of priorities in the index.
     */
    Mono<Long> getIndexCount(String keySuffix);

    /**
     * Gets {@link RuleMatchResults} based on the given id.
     *
     * @param keySuffix the suffix to use on the redis key.
     * @param id        the id to look up.
     * @return the results.
     */
    Mono<RuleMatchResults> getRuleMatchResults(String keySuffix, long id);
}
