package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(genreConverter::genreToDto).toList();
    }

    @Override
    public Optional<GenreDto> findById(String id) {
        return genreRepository.findById(id).map(genreConverter::genreToDto);
    }
}
