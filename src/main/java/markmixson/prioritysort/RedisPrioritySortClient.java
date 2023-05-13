package markmixson.prioritysort;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis client for priority sort.
 */
@Component
@RequiredArgsConstructor
@Getter
public class RedisPrioritySortClient {
    @NonNull
    private final PrioritySortMutationClient mutation;
    @NonNull
    private final PrioritySortQueryClient query;
}
