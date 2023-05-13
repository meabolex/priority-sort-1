package markmixson.prioritysort;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Query client for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortQueryClient {

    /**
     * Gets the top priority id from the index.
     *
     * @param suffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    Mono<Long> getTopPriority(@NonNull String suffix);

    /**
     * Gets the top N priorities ids from the index in order.
     *
     * @param suffix the suffix to use on the redis key.
     * @param count the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    Flux<Long> getTopPriorities(@NonNull String suffix, long count);

    /**
     * Gets the top priority id from the index.
     *
     * @param suffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the id with the top priority.
     */
    Mono<RuleMatchResults> getTopPriorityRuleMatchResult(@NonNull String suffix);

    /**
     * Gets the top N priorities ids from the index in order.
     *
     * @param suffix the suffix to use on the redis key.
     * @param count the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    Flux<RuleMatchResults> getTopPriorityRuleMatchResults(@NonNull String suffix, long count);

    /**
     * Gets the overall count of elements in the index.
     *
     * @param suffix the suffix to use on the redis key.
     * @return a {@link Mono} representing the count of priorities in the index.
     */
    Mono<Long> getIndexCount(@NonNull String suffix);

    /**
     * Gets {@link RuleMatchResults} based on the given id.
     *
     * @param suffix the suffix to use on the redis key.
     * @param id the id to look up.
     * @return the results.
     */
    Mono<RuleMatchResults> getRuleMatchResults(@NonNull String suffix, long id);

    /**
     * Gets all {@link RuleMatchResults}.
     *
     * @param suffix the suffix to use on the redis key.
     * @return the results.
     */
    Flux<RuleMatchResults> getAllRuleMatchResults(@NonNull String suffix);
}
