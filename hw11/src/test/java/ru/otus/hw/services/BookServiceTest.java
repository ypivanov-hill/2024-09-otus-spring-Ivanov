package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConvertor;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для книг должен")
@WebFluxTest(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BookController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommentController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GenreController.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = AuthorController.class)
})
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        CommentConvertor.class})
@TestPropertySource(properties = "mongock.enabled=false")
class BookServiceTest {

    private static final String NEW_BOOK_TITLE = "BookTitle_New";

    private List<Author> authors = List.of(new Author("AuthorId1", "Author 1 FullName"),
            new Author("AuthorId2", "Author 2 FullName"));

    private List<Genre> genres = List.of(new Genre("GenreId1", "Genre1"),
            new Genre("GenreId2", "Genre2"),
            new Genre("GenreId3", "Genre3"),
            new Genre("GenreId4", "Genre4"));

    private List<Book> books = List.of(new Book("BoolId1", "TestBook1", authors.get(0), List.of(genres.get(0), genres.get(1))),
            new Book("2L", "TestBook2", authors.get(1), List.of(genres.get(2), genres.get(3))));

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private GenreConverter genreConverter;


    @DisplayName("возвращать книгу по её id")
    @Test
    void shouldFindById() {
        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Mono.just(expectedBook));

        Mono<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book)
                        .isNotNull()
                        .hasFieldOrPropertyWithValue("title", expectedBook.getTitle()))
                .expectComplete()
                .verify();
    }


    @DisplayName("находить все книги")
    @Test
    void shouldFindAllBooks() {

        Flux<Book> booksFlux = Flux
                .fromIterable(books)
                .log();

        when(bookRepository.findAll()).thenReturn(booksFlux);

        int booksCount = booksFlux.collectList().block().size();

        Flux<BookDto> returnedBooks = bookService.findAll();

        StepVerifier
                .create(returnedBooks)
                .expectNextCount(booksCount)
                .expectComplete()
                .verify();
    }

    @DisplayName("добавлять новые книги")
    @Test
    void shouldInsertBook() {

        Book expectedBook = books.get(0);
        expectedBook.setTitle(NEW_BOOK_TITLE);
        when(bookRepository.findById(expectedBook.getId()))
                .thenReturn(Mono.just(expectedBook));

        Book newBook = new Book(null,
                NEW_BOOK_TITLE,
                expectedBook.getAuthor(),
                expectedBook.getGenres());

        Set<GenreDto> genreDtos = expectedBook
                .getGenres()
                .stream()
                .map(genreConverter::genreToDto)
                .collect(Collectors.toSet());

        when(bookRepository.save(newBook)).thenReturn(Mono.just(expectedBook));

        when(authorRepository.findById(newBook.getAuthor().getId()))
                .thenReturn(Mono.just(newBook.getAuthor()));

        Flux<Genre> genresFlux = Flux
                .fromIterable(expectedBook.getGenres())
                .log();

        when(genreRepository.findAllById(expectedBook.
                getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()))).
                thenReturn(genresFlux);

        Mono<BookDto> returnedBook = bookService.insert(newBook.getTitle(),
                new AuthorDto(expectedBook.getAuthor().getId(),
                        expectedBook.getAuthor().getFullName()),
                genreDtos);

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull()
                                    .matches(b -> b.getId() != null)
                                    .hasFieldOrPropertyWithValue("title", newBook.getTitle());

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(newBook.getAuthor().getId());

                            assertThat(book.getGenres())
                                    .hasSize(newBook.getGenres().size())
                                    .anyMatch(genre -> genre.getId().equals(newBook
                                            .getGenres()
                                            .get(0)
                                            .getId()));
                        }
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("изменять книги")
    @Test
    void shouldUpdateBook() {
        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId()))
                .thenReturn(Mono.just(expectedBook));

        Book newBook = expectedBook;

        newBook.setTitle(NEW_BOOK_TITLE);

        Set<GenreDto> genreDtos = expectedBook
                .getGenres()
                .stream()
                .map(genreConverter::genreToDto)
                .collect(Collectors.toSet());

        when(bookRepository.save(newBook)).thenReturn(Mono.just(newBook));

        when(authorRepository.findById(newBook.getAuthor().getId())).thenReturn(Mono.just(newBook.getAuthor()));

        Flux<Genre> genresFlux = Flux
                .fromIterable(expectedBook.getGenres())
                .log();

        when(genreRepository.findAllById(expectedBook.
                getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()))).
                thenReturn(genresFlux);

        Mono<BookDto> returnedBook = bookService.update(newBook.getId(),
                newBook.getTitle(),
                new AuthorDto(newBook.getAuthor().getId(),
                        newBook.getAuthor().getFullName()), genreDtos);


        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull()
                                    .matches(b -> b.getId() != null)
                                    .hasFieldOrPropertyWithValue("title", newBook.getTitle());

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(newBook.getAuthor().getId());

                            assertThat(book.getGenres())
                                    .hasSize(expectedBook.getGenres().size())
                                    .anyMatch(genre -> genre.getId().equals(newBook.getGenres().get(0).getId()));
                        }
                )
                .expectComplete()
                .verify();

    }

    @DisplayName("удалять книги и все комментарии по id")
    @Test
    void shouldDeleteBookById() {

        Book expectedBook = books.get(0);

        when(bookRepository.deleteById(expectedBook.getId())).thenReturn(Mono.empty());
        when(commentRepository.deleteByBookId(expectedBook.getId())).thenReturn(Mono.empty());


        var bookIdMono = bookService.deleteById(expectedBook.getId());

        StepVerifier
                .create(bookIdMono)
                .assertNext(book -> {
                            assertThat(book)
                                    .isNotNull()
                                    .isEqualTo(expectedBook.getId());
                        }
                )
                .expectComplete()
                .verify();

    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBook() {
        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Mono.just(expectedBook));

        Mono<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull();

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(expectedBook.getAuthor().getId());
                            ;
                        }
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен отображать жанры")
    @Test
    void shouldFindAllGenresInBook() {

        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Mono.just(expectedBook));

        int expectedGenresCount = expectedBook.getGenres().size();
        String expectedGenreId = expectedBook.getGenres().get(0).getId();

        Mono<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull();

                            assertThat(book.getGenres())
                                    .isNotNull()
                                    .hasSize(expectedGenresCount)
                                    .anyMatch(genre -> genre.getId().equals(expectedGenreId));
                        }
                )
                .expectComplete()
                .verify();
    }
}