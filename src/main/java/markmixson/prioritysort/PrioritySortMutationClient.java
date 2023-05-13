package markmixson.prioritysort;

import lombok.NonNull;
import reactor.core.publisher.Mono;

/**
 * Mutation client for priority sorting using redis sorted sets and hash sets.
 */
public interface PrioritySortMutationClient {

    /**
     * Adds or updates a {@link RuleMatchResults} in the index.
     *
     * @param results the result.
     * @return a {@link Mono} representing the number of added or updated values.
     */
    Mono<Long> addOrUpdate(@NonNull String suffix, @NonNull RuleMatchResults results);

    /**
     * Removes a {@link RuleMatchResults} from the index.
     *
     * @param id the id to remove.
     * @return a {@link Mono} representing the number of deleted items.
     */
    Mono<Long> delete(@NonNull String suffix, long id);

    /**
     * Clears all data from the index and the hashset.
     *
     * @return a {@link Mono} representing the number elements deleted.
     */
    Mono<Long> clear(@NonNull String suffix);
}
