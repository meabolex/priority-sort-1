package markmixson.prioritysort;

import io.lettuce.core.Range;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Client for making queries for priority sorts.
 */
@Component
@RequiredArgsConstructor
public class RedisPrioritySortQueryClient
        extends AbstractRedisPrioritySortClient
        implements PrioritySortQueryClient {

    @Getter(AccessLevel.PRIVATE)
    private final AsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool;

    @Override
    public Mono<Long> getTopPriority(@NonNull final String suffix) {
        return getTopPriorities(suffix, 1)
                .next();
    }

    @Override
    public Flux<Long> getTopPriorities(@NonNull final String suffix, final long count) {
        return getTopPriorityRuleMatchResults(suffix, count)
                .map(RuleMatchResults::id);
    }

    @Override
    public Mono<RuleMatchResults> getTopPriorityRuleMatchResult(@NonNull final String suffix) {
        return getTopPriorityRuleMatchResults(suffix, 1)
                .next();
    }

    @Override
    public Flux<RuleMatchResults> getTopPriorityRuleMatchResults(@NonNull final String suffix, final long count) {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMapMany(connection ->
                        connection.reactive().zrange(getIndexName(suffix), 0, count)
                                .doFinally(signal -> getConnectionPool().release(connection)))
                .map(RuleMatchResults::getRuleMatchResults);
    }

    @Override
    public Mono<Long> getIndexCount(@NonNull final String suffix) {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive().zcount(getIndexName(suffix), Range.unbounded())
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }
}
