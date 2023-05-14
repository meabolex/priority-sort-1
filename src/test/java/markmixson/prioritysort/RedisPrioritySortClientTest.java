package markmixson.prioritysort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
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
public class RedisPrioritySortClientTest {

    protected static final List<RuleMatchResults> RULE_MATCH_RESULTS_SCRAMBLED =
            List.of(THIRD, FOURTH, FIRST, FIFTH, SIXTH, SECOND);

    @Autowired
    RedisPrioritySortClients clients;

    protected void doAddOrUpdateTestData(@NonNull final String suffix) {
        RULE_MATCH_RESULTS_SCRAMBLED.parallelStream()
                .forEach(result -> getClients().getMutation().addOrUpdate(suffix, result)
                        .block());
    }
}
