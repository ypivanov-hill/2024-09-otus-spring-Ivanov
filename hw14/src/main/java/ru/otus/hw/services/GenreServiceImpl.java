package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final MappingService mappingService;

    private int idGenerator = 1;

    @Override
    public GenreNew getGenreNew(Genre genre) {
        mappingService.putGenreIds(idGenerator, genre.getId());
        return new GenreNew(idGenerator++, genre.getName());
    }


}
