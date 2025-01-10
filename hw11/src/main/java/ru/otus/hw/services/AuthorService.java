package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Flux<AuthorDto> findAll();

    Mono<AuthorDto> findById(String id);

    void deleteById(String id);
}
