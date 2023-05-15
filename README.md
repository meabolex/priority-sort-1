# Priority Sort Tool using Redis

This project leverages the binary lexicographic sorting done by a Redis sorted set to quickly determine a priority ordering based on binary matches to rules.  It then uses a date fallback to order if all rules match.  It uses the following tech stack:

* Spring Boot 3.1.0 with Java 20
* Spring Data Redis Reactive, but mostly using the Lettuce Redis client directly.
* Testcontainers/Jacoco/Pitest/Junit for tests.
