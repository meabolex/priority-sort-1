package markmixson.prioritysort;

import java.util.Objects;

import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.lettuce.core.Range;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.BoundedAsyncPool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class RedisPrioritySortClient implements PrioritySortClient {

    @Language("lua")
    private static final String ADD_UPDATE_LUA_SCRIPT = """
            local indexname, setname, id = unpack(KEYS)
            local data = unpack(ARGV)
            local previous = redis.call('hget', setname, id)
            if previous then
                redis.call('zrem', indexname, previous)
                redis.call('hdel', setname, id)
            end
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

    private final BoundedAsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool;

    @SuppressWarnings("SpringElInspection")
    @Value("${priority-sort.index-name:prioritysort}")
    private String prioritySortIndexName;

    @SuppressWarnings("SpringElInspection")
    @Value("${priority-sort.set-name:prioritysort.content}")
    private String prioritySortSetName;

    @Override
    public Mono<Long> addOrUpdate(final RuleMatchResults results) {
        Objects.requireNonNull(results, "results cannot be empty!");
        final var keys = new String[] { getPrioritySortIndexName(), getPrioritySortSetName(), results.id().toString() };
        final var values = new byte[][] { results.toByteArray() };
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive()
                        .<Long>eval(ADD_UPDATE_LUA_SCRIPT, ScriptOutputType.INTEGER, keys, values).next()
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }

    @Override
    public Mono<Long> delete(long id) {
        final var keys = new String[] { getPrioritySortIndexName(), getPrioritySortSetName(), Long.toString(id) };
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive()
                        .<Long>eval(DEL_LUA_SCRIPT, ScriptOutputType.INTEGER, keys).next()
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }

    @Override
    public Mono<Long> clear() {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive().del(getPrioritySortIndexName(), getPrioritySortSetName())
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }

    @Override
    public Mono<Long> getTopPriority() {
        return getTopPriorities(1).next();
    }

    @Override
    public Flux<Long> getTopPriorities(long count) {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMapMany(connection -> connection.reactive().zrange(getPrioritySortIndexName(), 0, count)
                        .doFinally(signal -> getConnectionPool().release(connection)))
                .map(RuleMatchResults::getRuleMatchResults)
                .map(RuleMatchResults::id);
    }

    @Override
    public Mono<Long> getIndexCount() {
        return Mono.fromFuture(() -> getConnectionPool().acquire())
                .flatMap(connection -> connection.reactive().zcount(getPrioritySortIndexName(), Range.unbounded())
                        .doFinally(signal -> getConnectionPool().release(connection)));
    }
}
