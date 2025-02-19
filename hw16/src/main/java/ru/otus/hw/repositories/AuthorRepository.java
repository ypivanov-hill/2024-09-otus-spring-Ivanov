package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "author")
public interface AuthorRepository extends MongoRepository<Author, String>, AuthorRepositoryCustom {

    List<Author> findAll();

    Optional<Author> findById(String id);

    void deleteById(String id);

}
