package markmixson.prioritysort;

import io.lettuce.core.Range;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for making queries for priority sorts.
 */
@Component
public class RedisPrioritySortQueryClient
        extends AbstractRedisPrioritySortClient
        implements PrioritySortQueryClient {

    /**
     * Sets up client with connection pool.
     *
     * @param pool the connection pool.
     */
    public RedisPrioritySortQueryClient(@NonNull final AsyncPool<StatefulRedisConnection<String, byte[]>> pool) {
        super(pool);
    }

    @Override
    public Flux<RuleMatchResults> getTopPriorityRuleMatchResults(@NonNull final String keySuffix, final long count) {
        return runMany(redis -> redis.zrange(getIndexName(keySuffix), 0, count))
                .map(RuleMatchResults::getRuleMatchResults);
    }

    @Override
    public Flux<Long> getTopPriorities(@NonNull final String keySuffix, final long count) {
        return getTopPriorityRuleMatchResults(keySuffix, count)
                .map(RuleMatchResults::id);
    }

    @Override
    public Mono<Long> getTopPriority(@NonNull final String keySuffix) {
        return getTopPriorities(keySuffix, 1)
                .next();
    }

    @Override
    public Mono<RuleMatchResults> getTopPriorityRuleMatchResult(@NonNull final String keySuffix) {
        return getTopPriorityRuleMatchResults(keySuffix, 1)
                .next();
    }

    @Override
    public Mono<Long> getIndexCount(@NonNull final String keySuffix) {
        return runSingle(redis -> redis.zcount(getIndexName(keySuffix), Range.unbounded()));
    }

    @Override
    public Mono<RuleMatchResults> getRuleMatchResults(@NonNull final String keySuffix, long id) {
        return runSingle(redis -> redis.hget(getSetName(keySuffix), Long.toString(id)))
                .map(RuleMatchResults::getRuleMatchResults);
    }

    @Override
    public Flux<RuleMatchResults> getAllRuleMatchResults(@NonNull final String keySuffix) {
        return runMany(redis -> redis.hvals(getSetName(keySuffix)))
                .map(RuleMatchResults::getRuleMatchResults);
    }
}
