package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private long startIdValue;

    private long endIdValue;

    @Override
    public GenreNew getGenreNew(Genre genre) {
        mappingService.putGenreIds(startIdValue, genre.getId());
        return new GenreNew(startIdValue++, genre.getName());
    }

    @Override
    public void reserveSequenceValues() {

        startIdValue = jdbc.queryForObject("VALUES NEXT VALUE FOR GENRES_SEQ", Map.of(), Integer.class);
        long countAuthors = genreRepository.count();
        endIdValue = startIdValue + countAuthors;
        jdbc.update("ALTER SEQUENCE GENRES_SEQ restart with :newSeqStart",
                Map.of("newSeqStart", endIdValue));
    }

}
