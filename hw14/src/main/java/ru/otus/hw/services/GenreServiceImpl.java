package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private static final String SEQUENCE_NAME = "GENRES_SEQ";

    private final MappingService mappingService;

    private final SequenceValueService sequenceValueService;

    private final GenreRepository genreRepository;

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

        reservedIds = sequenceValueService.getSequenceValues(genreRepository.count(), SEQUENCE_NAME);
    }
}
