package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;


    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(authorConverter::authorToDto);
    }

    @Override
    public Mono<AuthorDto> findById(String id) {
        Mono<Author> author =  authorRepository.findById(id);
        return author.map(authorConverter::authorToDto);
    }

    @Override
    public void deleteById(String id) {
        authorRepository.deleteAuthorById(id);
    }


}
