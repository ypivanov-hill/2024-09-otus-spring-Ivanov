package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String>, AuthorRepositoryCustom {

    Flux<Author> findAll();

    Mono<Author> findById(String id);

    void deleteAuthorById(String id);

}
