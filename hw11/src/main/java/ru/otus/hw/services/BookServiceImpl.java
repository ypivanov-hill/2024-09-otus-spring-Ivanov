package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    //private final Scheduler workerPool;

    @Override
    public Mono<BookDto> findById(String id) {
        Mono<Book>  book = bookRepository.findById(id);
        return book.map(bookConverter::bookToDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        Flux<Book> books = bookRepository.findAll();
        return books.map(bookConverter::bookToDto);
    }

    @Override
    public Mono<BookDto> insert(String title, AuthorDto author, Set<GenreDto> genres) {
        return save(null, title, author, genres);
    }

    @Override
    public Mono<BookDto> update(String id, String title, AuthorDto author, Set<GenreDto> genres) {
        return save(id, title, author, genres);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteBookById(id);
    }


    private Mono<BookDto> save(String id, String title,  AuthorDto authorDto, Set<GenreDto> genreDtos) {

        if (isEmpty(genreDtos)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        //Book book;
        var author = authorRepository.findById(authorDto.getId())/*
                .subscribe(author1 -> {
                    var  author2 = author1;
                    book = new  Book(id, title, author2, List.of());
                })*/;

               // .orElseThrow(() -> new IllegalArgumentException("Author %s not found"
               //         .formatted(authorDto.getFullName())));
        var genres = genreRepository.findAllById(genreDtos.stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet()));
        /*if (genres. || genres.size() != genreDtos.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found"
                    .formatted(genreDtos));
        }*/

        var book = new Book(id, title, author, genres);

        return bookRepository.save(book).map(bookConverter::bookToDto);
    }

    public Flux<BookCountByGenreDto> getBookCountByGenre() {
        return bookRepository.getBookCountByGenre();
    }
}
