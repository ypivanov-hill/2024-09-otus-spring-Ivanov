package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "book")
public interface BookRepository extends MongoRepository<Book, String>, BookRepositoryCustom {
    Optional<Book> findById(String id);

    List<Book> findAll();

    @RestResource(path = "titles", rel = "titles")
    Optional<Book> findByTitleIgnoreCase(String title);
}
