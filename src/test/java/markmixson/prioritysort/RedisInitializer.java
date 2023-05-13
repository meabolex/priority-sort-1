package markmixson.prioritysort;

import java.util.List;

import lombok.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final int CONTAINER_REDIS_PORT = 6379;
    private static final String IMAGE_NAME = "redis:7-alpine";
    private static final DockerImageName REDIS_IMAGE_NAME = DockerImageName.parse(IMAGE_NAME);

    @SuppressWarnings("resource")
    static GenericContainer<?> REDIS = new GenericContainer<>(REDIS_IMAGE_NAME)
            .withExposedPorts(CONTAINER_REDIS_PORT)
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1))
            .withEnv("REDIS_ARGS", String.format("--io-threads %d --save \"\" --appendonly no",
                    Runtime.getRuntime().availableProcessors() / 2));

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context) {
        final var binding = String.format("%d:%d", CONTAINER_REDIS_PORT, CONTAINER_REDIS_PORT);
        REDIS.setPortBindings(List.of(binding));
        REDIS.start();
    }
}
