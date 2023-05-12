package markmixson.prioritysort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client interface for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortClient {
    /**
     * Adds or updates a {@link RuleMatchResults} in the index.
     * 
     * @param results the result.
     * @return a {@link Mono} representing the number of added or updated values.
     */
    Mono<Long> addOrUpdate(RuleMatchResults results);

    /**
     * Removes a {@link RuleMatchResults} from the index.
     * 
     * @param id the id to remove.
     * @return a {@link Mono} representing the number of deleted items.
     */
    Mono<Long> delete(long id);

    /**
     * Clears all data from the index and the hashset.
     * 
     * @return a {@link Mono} representing the number elements deleted.
     */
    Mono<Long> clear();

    /**
     * Gets the top priority id from the index.
     * 
     * @return a {@link Mono} representing the id with the top priority.
     */
    Mono<Long> getTopPriority();

    /**
     * Gets the top N priorities ids from the index in order.
     * 
     * @param count the number of priorities to get.
     * @return a {@link Flux} representing the top N priority ids.
     */
    Flux<Long> getTopPriorities(long count);

    /**
     * Gets the overall count of elements in the index.
     * 
     * @return a {@link Mono} representing the count of priorities in the index.
     */
    Mono<Long> getIndexCount();
}
