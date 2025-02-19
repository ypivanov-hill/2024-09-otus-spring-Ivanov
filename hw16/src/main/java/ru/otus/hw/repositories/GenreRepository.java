package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource(path = "genre")
public interface GenreRepository extends MongoRepository<Genre, String> {

    List<Genre> findAll();

    @RestResource(path = "ids", rel = "ids")
    List<Genre> findAllByIdIn(Set<String> ids);

    @RestResource(path = "names", rel = "names")
    Optional<Genre> findFirstByName(String name);
}
