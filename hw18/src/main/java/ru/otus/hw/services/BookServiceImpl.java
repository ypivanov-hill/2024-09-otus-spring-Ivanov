package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Override
    @CircuitBreaker(name = "defaultCircuitBreaker")
    @RateLimiter(name = "defaultRateLimiter")
    public Optional<BookDto> findById(String id) {
        Optional<Book>  book = bookRepository.findById(id);
        return book.map(bookConverter::bookToDto);
    }

    /*@Override
    public List<BookDto> findAll() {
        log.info("findAll start");
        CheckedSupplier<List<Book>> restrictedSupplier = RateLimiter.decorateCheckedSupplier(rateLimiter, () ->
                circuitBreaker.run(() -> bookRepository.findAll(), t -> {
                    log.error("delay call failed error:{}", t.getMessage());
                    return Collections.emptyList();
                })
        );
        List<Book> books = Collections.emptyList();
        try {
            books = restrictedSupplier.get();
        } catch (Throwable throwable) {
            log.error("Failed to retrieve books: {}", throwable.getMessage());
        }
        log.info("findAll books: {}", books.size());
        return books.stream()
                .map(bookConverter::bookToDto)
                .toList();
    }*/

    @CircuitBreaker(name = "defaultCircuitBreaker")
    @RateLimiter(name = "defaultRateLimiter")
    @Override
    public List<BookDto> findAll() {
        log.info("findAll start");
       return bookRepository.findAll().stream()
               .map(bookConverter::bookToDto)
                .toList();
}

    @Override
    public BookDto insert(String title, AuthorDto author, Set<GenreDto> genres) {
        return save(null, title, author, genres);
    }

    @Override
    public BookDto update(String id, String title, AuthorDto author, Set<GenreDto> genres) {
        return save(id, title, author, genres);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteBookById(id);
    }


    private BookDto save(String id, String title,  AuthorDto authorDto, Set<GenreDto> genreDtos) {
        if (isEmpty(genreDtos)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Author %s not found"
                        .formatted(authorDto.getFullName())));
        var genres = genreRepository.findAllById(genreDtos.stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet()));
        if (isEmpty(genres) || genres.size() != genreDtos.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found"
                    .formatted(genreDtos));
        }

        var book = new Book(id, title, author, genres);
        return bookConverter.bookToDto(bookRepository.save(book));
    }

    public List<BookCountByGenreDto> getBookCountByGenre() {
        return bookRepository.getBookCountByGenre();
    }
}
