package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Mono<Comment> findById(String id);

    Flux<Comment> findByBookId(String bookId);

    Mono<Comment> save(Comment comment);

    Mono<Void> deleteById(String id);

}
