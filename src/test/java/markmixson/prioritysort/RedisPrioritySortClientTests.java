package markmixson.prioritysort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIFTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FIRST;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.FOURTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SECOND;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.SIXTH;
import static markmixson.prioritysort.RedisPrioritySortClientTestData.THIRD;

@SpringBootTest
@Getter(AccessLevel.PROTECTED)
@ContextConfiguration(initializers = RedisInitializer.class)
public class RedisPrioritySortClientTests {

    protected static final List<RuleMatchResults> RULE_MATCH_RESULTS_SCRAMBLED =
            List.of(THIRD, FOURTH, FIRST, FIFTH, SIXTH, SECOND);

    @Autowired
    RedisPrioritySortClient client;

    protected void doAddOrUpdateTestData(@NonNull final String suffix) {
        final var results = RULE_MATCH_RESULTS_SCRAMBLED.parallelStream()
                .map(result -> getClient().mutation().addOrUpdate(suffix, result).block())
                .toList();
        Assertions.assertEquals(RULE_MATCH_RESULTS_SCRAMBLED.size(), results.size());
    }
}
