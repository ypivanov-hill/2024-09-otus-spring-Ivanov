package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.in.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository extends MongoRepository<Genre, String> {
    List<Genre> findAll();

    List<Genre> findAllByNameIn(Set<String> ids);

    Optional<Genre> findFirstByName(String name);
}
