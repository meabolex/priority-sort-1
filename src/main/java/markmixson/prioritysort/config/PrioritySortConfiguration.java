package markmixson.prioritysort.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.Futures;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.BoundedAsyncPool;
import io.lettuce.core.support.BoundedPoolConfig;
import lombok.Getter;

/**
 * Redis connection pool settings.
 */
@Configuration
@Getter
public class PrioritySortConfiguration {

    @SuppressWarnings("SpringElInspection")
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("#{new Integer('${spring.data.redis.port:6379}')}")
    private Integer redisPort;

    /**
     * The Redis Connection Pool settings.
     * 
     * @return a {@link BoundedAsyncPool} for connections.
     */
    @Bean
    public BoundedAsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool() {
        final var uri = RedisURI.create(getRedisHost(), getRedisPort());
        @SuppressWarnings("resource") final var client = RedisClient.create(uri);
        final var codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
        final var config = BoundedPoolConfig.builder()
                .maxTotal(Runtime.getRuntime().availableProcessors())
                .build();
        return Futures.getUnchecked(AsyncConnectionPoolSupport.createBoundedObjectPoolAsync(
                () -> client.connectAsync(codec, uri), config).toCompletableFuture());
    }
}
