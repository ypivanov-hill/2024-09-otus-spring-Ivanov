package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "comment")
public interface CommentRepository extends MongoRepository<Comment, String> {

    Optional<Comment> findById(String id);

    @RestResource(path = "book-id", rel = "by-book-id")
    List<Comment> findByBookId(String bookId);

    Comment save(Comment comment);

    void deleteById(String id);

}
