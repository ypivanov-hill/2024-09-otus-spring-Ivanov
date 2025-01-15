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
    public void deleteById(String id) {
        bookRepository.deleteBookById(id);
    }


    private Mono<BookDto> save(String id, String title,  AuthorDto authorDto, Set<GenreDto> genreDtos) {
        if (isEmpty(genreDtos)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        Mono<Author> author = authorRepository.findById(authorDto.getId());
        Mono<List<Genre>> genres = genreRepository.findAllById(genreDtos.stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet())).collectList();
        Mono<Book> newBook = Mono.zip(author, genres, (author1, genreList) -> new Book(id, title, author1, genreList));
        newBook = newBook.flatMap(book -> {
            commentRepository.findByBookId(book.getId()).subscribe(
                    comment -> {
                        comment.setBook(book);
                        commentRepository
                                .save(comment)
                                .subscribe();
                    }
            );
            return bookRepository.save(book);
        });
        return newBook.map(bookConverter::bookToDto);
    }

    public Flux<BookCountByGenreDto> getBookCountByGenre() {
        return bookRepository.getBookCountByGenre();
    }
}
