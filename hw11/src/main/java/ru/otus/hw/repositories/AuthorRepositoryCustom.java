package ru.otus.hw.repositories;

import reactor.core.publisher.Mono;

public interface AuthorRepositoryCustom {

    Mono<String> deleteAuthorById(String id);
}
