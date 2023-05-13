package markmixson.prioritysort.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.AsyncPool;
import io.lettuce.core.support.BoundedPoolConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis connection pool settings.
 */
@Configuration
@Getter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class PrioritySortConfiguration {

    @SuppressWarnings("SpringElInspection")
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("#{new Integer('${spring.data.redis.port:6379}')}")
    private Integer redisPort;

    /**
     * The Redis Connection Pool settings.
     *
     * @return a {@link AsyncPool} for connections.
     */
    @Bean
    @SuppressWarnings("resource")
    public AsyncPool<StatefulRedisConnection<String, byte[]>> connectionPool() {
        final var uri = RedisURI.create(getRedisHost(), getRedisPort());
        return AsyncConnectionPoolSupport.createBoundedObjectPool(() -> RedisClient.create(uri)
                        .connectAsync(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE), uri),
                BoundedPoolConfig.builder()
                        .maxTotal(Runtime.getRuntime().availableProcessors())
                        .build());
    }
}
