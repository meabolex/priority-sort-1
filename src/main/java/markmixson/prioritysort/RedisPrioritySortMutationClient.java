package markmixson.prioritysort;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Client for making mutations for priority sorts.
 */
@Component
@RequiredArgsConstructor
public class RedisPrioritySortMutationClient
        extends AbstractRedisPrioritySortClient
        implements PrioritySortMutationClient {

    @Language("lua")
    private static final String ADD_UPDATE_LUA_SCRIPT = """
            local indexname, setname, id = unpack(KEYS)
            local previous = redis.call('hget', setname, id)
            if previous then
                redis.call('zrem', indexname, previous)
                redis.call('hdel', setname, id)
            end
            local data = unpack(ARGV)
            local output = redis.call('zadd', indexname, 0, data)
            redis.call('hset', setname, id, data)
            return output
            """;

    @Language("lua")
    private static final String DEL_LUA_SCRIPT = """
            local indexname, setname, id = unpack(KEYS)
            local byteData = redis.call('hget', setname, id)
            local output = redis.call('zrem', indexname, byteData)
            redis.call('hdel', setname, id)
            return output
            """;

    @Getter(AccessLevel.PRIVATE)
    private final AsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool;

    @Override
    public Mono<Long> addOrUpdate(@NonNull final String suffix, @NonNull final RuleMatchResults results) {
        final var keys = new String[]{getIndexName(suffix), getSetName(suffix), results.id().toString()};
        final var values = new byte[][]{results.toByteArray()};
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive()
                        .<Long>eval(ADD_UPDATE_LUA_SCRIPT, ScriptOutputType.INTEGER, keys, values).next()
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }

    @Override
    public Mono<Long> delete(@NonNull final String suffix, final long id) {
        final var keys = new String[]{getIndexName(suffix), getSetName(suffix), Long.toString(id)};
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive()
                        .<Long>eval(DEL_LUA_SCRIPT, ScriptOutputType.INTEGER, keys).next()
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }

    @Override
    public Mono<Long> clear(@NonNull final String suffix) {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive().del(getIndexName(suffix), getSetName(suffix))
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }
}
