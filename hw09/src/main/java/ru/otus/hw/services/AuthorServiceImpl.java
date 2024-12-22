package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorConverter authorConverter;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream().map(authorConverter::authorToDto).toList();
    }

    @Override
    public Optional<AuthorDto> findById(String id) {
        return authorRepository.findById(id).map(authorConverter::authorToDto);
    }

    @Override
    public List<Author> findByFullName(String fullName) {
        return authorRepository.findByFullName(fullName);
    }

    @Override
    public void deleteByFullName(String fullName) {
        authorRepository.deleteByFullName(fullName);
    }
}
