package ru.otus.hw.services;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;

public interface AuthorService {
    @Operation(description = "get all the Authors")
    Flux<AuthorDto> findAll();

    @Operation(description = "get one Author by id")
    Mono<AuthorDto> findById(String id);

    void deleteById(String id);
}
