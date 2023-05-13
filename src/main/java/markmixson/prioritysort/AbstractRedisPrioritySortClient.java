package markmixson.prioritysort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Base class for Redis priority sort clients.
 */
@Component
@NoArgsConstructor
@Getter(AccessLevel.PRIVATE)
@SuppressWarnings("SpringElInspection")
public abstract class AbstractRedisPrioritySortClient {

    @Value("${priority-sort.index-name:prioritysort}")
    private String prioritySortIndexName;

    @Value("${priority-sort.set-name:prioritysort.content}")
    private String prioritySortSetName;

    /**
     * Gets expected index name format in Redis.
     *
     * @param suffix suffix to add to name.
     * @return the index name.
     */
    protected String getIndexName(@NonNull final String suffix) {
        return String.format("%s.%s", getPrioritySortIndexName(), suffix);
    }

    /**
     * Gets expected set name format in Redis.
     * @param suffix suffix to add to name.
     * @return the index name.
     */
    protected String getSetName(@NonNull final String suffix) {
        return String.format("%s.%s", getPrioritySortSetName(), suffix);
    }
}
