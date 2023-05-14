package markmixson.prioritysort;

import io.lettuce.core.Range;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for making queries for priority sorts.
 */
@Component
public class RedisPrioritySortQueryClient
        extends RedisPrioritySortClient
        implements PrioritySortQueryClient {

    /**
     * Sets up client with connection pool.
     *
     * @param pool the connection pool.
     */
    public RedisPrioritySortQueryClient(final AsyncPool<StatefulRedisConnection<String, byte[]>> pool) {
        super(pool);
    }

    @Override
    public Flux<RuleMatchResults> getTopPriorityRuleMatchResults(final String keySuffix, final long count) {
        return runMany(redis -> redis.zrange(getIndexName(keySuffix), 0, count))
                .map(RuleMatchResults::getRuleMatchResults);
    }

    @Override
    public Flux<Long> getTopPriorities(final String keySuffix, final long count) {
        return getTopPriorityRuleMatchResults(keySuffix, count)
                .map(RuleMatchResults::id);
    }

    @Override
    public Mono<Long> getTopPriority(final String keySuffix) {
        return getTopPriorities(keySuffix, 1)
                .next();
    }

    @Override
    public Mono<RuleMatchResults> getTopPriorityRuleMatchResult(final String keySuffix) {
        return getTopPriorityRuleMatchResults(keySuffix, 1)
                .next();
    }

    @Override
    public Mono<Long> getIndexCount(final String keySuffix) {
        return runSingle(redis -> redis.zcount(getIndexName(keySuffix), Range.unbounded()));
    }

    @Override
    public Mono<RuleMatchResults> getRuleMatchResults(final String keySuffix, long id) {
        return runSingle(redis -> redis.hget(getSetName(keySuffix), Long.toString(id)))
                .map(RuleMatchResults::getRuleMatchResults);
    }
}
