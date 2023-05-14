package markmixson.prioritysort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis client for priority sort.
 */
@Component
@RequiredArgsConstructor
@Getter
public class RedisPrioritySortClients {
    private final PrioritySortMutationClient mutation;
    private final PrioritySortQueryClient query;
}
