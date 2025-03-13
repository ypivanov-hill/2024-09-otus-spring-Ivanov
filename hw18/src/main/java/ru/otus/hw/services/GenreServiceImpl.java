package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.aspect.RateLimitedAndCircuitBreaker;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override@CircuitBreaker(name = "defaultCircuitBreaker")
    @RateLimiter(name = "defaultRateLimiter")
    //@RateLimitedAndCircuitBreaker(rateLimiterName = "bookRateLimiter", circuitBreakerName = "bookCircuitBreaker")
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(genreConverter::genreToDto)
                .toList();
    }
}
