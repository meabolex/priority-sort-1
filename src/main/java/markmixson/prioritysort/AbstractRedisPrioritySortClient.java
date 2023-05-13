package markmixson.prioritysort;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.support.AsyncPool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Base class for Redis priority sort clients.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PRIVATE)
@SuppressWarnings("SpringElInspection")
public abstract class AbstractRedisPrioritySortClient {

    @Value("${priority-sort.index-name-prefix:prioritysort}")
    private String indexNamePrefix;

    @Value("${priority-sort.set-name-prefix:prioritysort.content}")
    private String setNamePrefix;

    private final AsyncPool<StatefulRedisConnection<String, byte[]>> pool;

    /**
     * Given a Redis command to run that has a single result, apply the command to a Redis connection pool.
     *
     * @param toRun the command to run
     * @param <T>   the type of the result
     * @return the result
     */
    protected <T> Mono<T> runSingle(
            @NonNull final Function<RedisReactiveCommands<String, byte[]>, Mono<T>> toRun) {
        return Mono.fromFuture(() -> getPool().acquire())
                .flatMap(connection -> toRun.apply(connection.reactive())
                        .doFinally(signal -> getPool().release(connection)));
    }

    /**
     * Given a Redis command to run that has possibly many results, apply the command to a Redis connection pool.
     *
     * @param toRun the command to run
     * @param <T>   the type of the result
     * @return the results
     */
    protected <T> Flux<T> runMany(
            @NonNull final Function<RedisReactiveCommands<String, byte[]>, Flux<T>> toRun) {
        return Mono.fromFuture(() -> getPool().acquire())
                .flatMapMany(connection -> toRun.apply(connection.reactive())
                        .doFinally(signal -> getPool().release(connection)));
    }

    /**
     * Gets expected index name format in Redis.
     *
     * @param suffix suffix to add to name.
     * @return the index name.
     */
    protected String getIndexName(@NonNull final String suffix) {
        return String.format("%s.%s", getIndexNamePrefix(), suffix);
    }

    /**
     * Gets expected set name format in Redis.
     *
     * @param suffix suffix to add to name.
     * @return the set name.
     */
    protected String getSetName(@NonNull final String suffix) {
        return String.format("%s.%s", getSetNamePrefix(), suffix);
    }
}
