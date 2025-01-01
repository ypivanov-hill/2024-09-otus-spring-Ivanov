package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final MappingService mappingService;

    private final NamedParameterJdbcOperations jdbc;

    private List<Long> reservedIds;

    @Override
    public GenreNew getGenreNew(Genre genre) {
        if (reservedIds.isEmpty()) {
            throw new RuntimeException("There is no id to create new rows");
        }
        Long currentId  = reservedIds.get(0);
        reservedIds.remove(currentId);
        mappingService.putGenreIds(currentId, genre.getId());
        return new GenreNew(currentId, genre.getName());
    }

    @Override
    public void reserveSequenceValues() {

        long countAuthors = genreRepository.count();

        reservedIds = jdbc.queryForList("select nextval('GENRES_SEQ') from SYSTEM_RANGE(1, :cnt)",
                Map.of("cnt", countAuthors),
                Long.class);
    }
}
