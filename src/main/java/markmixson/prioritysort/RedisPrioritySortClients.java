package markmixson.prioritysort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis clients for priority sort.
 */
@Component
@RequiredArgsConstructor
@Getter
public class RedisPrioritySortClients {

    /**
     * Client for mutations.
     */
    private final PrioritySortMutationClient mutation;

    /**
     * Client for queries.
     */
    private final PrioritySortQueryClient query;
}
