package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Component
public class BookCountHealthIndicator implements HealthIndicator {

    private static final long CONST_BOOK_COUNT = 3;

    private final BookRepository bookRepository;

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        long currentBookCount = bookRepository.count();
        if (currentBookCount == CONST_BOOK_COUNT) {
            return Health.up().withDetail("Book count", currentBookCount).build();
        } else {
            return Health
                    .down()
                    .withDetail("Current book count", currentBookCount)
                    .withDetail("Expected book count", CONST_BOOK_COUNT)
                    .build();
        }
    }
}
