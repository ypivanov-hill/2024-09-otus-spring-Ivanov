package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final BookConverter bookConverter;

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
    public Mono<String> deleteById(String id) {
        return bookRepository.deleteById(id).then(commentRepository.deleteByBookId(id)).thenReturn(id);
    }

    private Mono<BookDto> save(String id, String title,  AuthorDto authorDto, Set<GenreDto> genreDtos) {
        if (isEmpty(genreDtos)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        Mono<Author> author = authorRepository.findById(authorDto.getId());
        Mono<Book> book;
        if (id == null) {
            book = Mono.just(new Book(null, null, null, null));
        } else {
            book = bookRepository.findById(id);
        }
        Mono<List<Genre>> genres = genreRepository.findAllById(genreDtos.stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet())).collectList();

       return Mono.zip(book, author, genres).flatMap(data -> {
            Book currentBook = data.getT1();
                   currentBook.setTitle(title);
                   currentBook.setAuthor(data.getT2());
                   currentBook.setGenres(data.getT3());
            return Mono.just(currentBook);
        }).flatMap(bookRepository::save)
                .map(bookConverter::bookToDto);
    }

    public Flux<BookCountByGenreDto> getBookCountByGenre() {
        return bookRepository.getBookCountByGenre();
    }
}
