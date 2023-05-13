package markmixson.prioritysort;

import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Redis client for priority sort.
 * @param mutation mutation client
 * @param query query client
 */
@Component
@SuppressWarnings("unused")
public record RedisPrioritySortClient(
        @NonNull PrioritySortMutationClient mutation,
        @NonNull PrioritySortQueryClient query) {
}
