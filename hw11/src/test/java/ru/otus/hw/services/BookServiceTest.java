package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для книг должен")
@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
class BookServiceTest {

    private static final String FIRST_BOOK_NAME = "BookTitle_1";
    private static final String SECOND_BOOK_NAME = "BookTitle_2";
    private static final String THIRD_BOOK_NAME = "BookTitle_3";

    private static final String NEW_BOOK_TITLE = "BookTitle_New";


    private static final String FIRST_AUTHOR_FULL_NAME = "Author_1";

    private static final String FIRST_GENRE_NAME = "Genre_1";
    private static final String SECOND_GENRE_NAME = "Genre_2";

    @Autowired
    private BookService bookService;

    @Autowired
    private ReactiveMongoOperations mongoTemplate;

    @Autowired
    private GenreConverter genreConverter;

    @DisplayName("возвращать книгу по её id")
    @Order(1)
    @Test
    void shouldFindById() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull();

        Mono<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book)
                        .isNotNull()
                        .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME))
                .expectComplete()
                .verify();
    }


    @DisplayName("находить все книги")
    @Order(2)
    @Test
    void shouldFindAllBooks() {

        var expectedBooks = mongoTemplate.findAll(Book.class).collectList().block();
        Flux<BookDto> returnedBooks = bookService.findAll();

        StepVerifier
                .create(returnedBooks)
                .expectNextCount(expectedBooks.size())
                .expectComplete()
                .verify();
    }

    @DisplayName("добавлять новые книги")
    @Order(3)
    @Test
    void shouldInsertBook() {

        List<Book> expectedBooks = mongoTemplate.findAll(Book.class).collectList().block();

        Query query = new Query(Criteria.where("fullName").is(FIRST_AUTHOR_FULL_NAME));
        var expectedAuthor = mongoTemplate.findOne(query, Author.class).block();
        assertThat(expectedAuthor).isNotNull();

        query = new Query(Criteria.where("name").is(FIRST_GENRE_NAME));
        var expectedGenre = genreConverter.genreToDto(mongoTemplate.findOne(query, Genre.class).block());
        assertThat(expectedGenre).isNotNull();

        int beforeInsetCount = expectedBooks.size();

        Mono<BookDto> returnedBook = bookService.insert(NEW_BOOK_TITLE,
                new AuthorDto(expectedAuthor.getId(),
                        expectedAuthor.getFullName()),
                Set.of(expectedGenre));

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull()
                                    .matches(b -> b.getId() != null)
                                    .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(expectedAuthor.getId());

                            assertThat(book.getGenres())
                                    .hasSize(1)
                                    .anyMatch(genre -> genre.getId().equals(expectedGenre.getId()));
                        }
                )
                .expectComplete()
                .verify();


        expectedBooks = mongoTemplate.findAll(Book.class).collectList().block();
        assertThat(expectedBooks).isNotEmpty()
                .hasSize(beforeInsetCount + 1);
    }

    @DisplayName("изменять книги")
    @Order(4)
    @Test
    void shouldUpdateBook() {
        Query query = new Query(Criteria.where("title").is(THIRD_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull();

        query = new Query(Criteria.where("fullName").is(FIRST_AUTHOR_FULL_NAME));
        var expectedAuthor = mongoTemplate.findOne(query, Author.class).block();
        assertThat(expectedAuthor).isNotNull();

        query = new Query(Criteria.where("name").is(FIRST_GENRE_NAME));
        var expectedGenre = genreConverter.genreToDto(mongoTemplate.findOne(query, Genre.class).block());
        assertThat(expectedGenre).isNotNull();

        Mono<BookDto> returnedBook = bookService.update(expectedBook.getId(),
                NEW_BOOK_TITLE,
                new AuthorDto(expectedAuthor.getId(),
                        expectedAuthor.getFullName()), Set.of(expectedGenre));


        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull()
                                    .matches(b -> b.getId() != null)
                                    .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(expectedAuthor.getId());

                            assertThat(book.getGenres())
                                    .hasSize(1)
                                    .anyMatch(genre -> genre.getId().equals(expectedGenre.getId()));
                        }
                )
                .expectComplete()
                .verify();

    }

    @DisplayName("удалять книги и все комментарии по id")
    @Order(7)
    @Test
    void shouldDeleteBookById() {

        Query query = new Query(Criteria.where("title").is(SECOND_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull();

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

        Query queryComments = new Query(Criteria.where("book._id").is(expectedBook.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class).collectList().block();
        assertThat(comments).isEmpty();

    }

    @DisplayName("должен отображать автора")
    @Order(5)
    @Test
    void shouldFindAuthorInBook() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull().extracting(Book::getAuthor).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

        String expectedAuthorId = expectedBook.getAuthor().getId();


        Mono<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> {
                            assertThat(book).isNotNull();

                            assertThat(book.getAuthor().getId()).isNotNull()
                                    .hasToString(expectedAuthorId);
                            ;
                        }
                )
                .expectComplete()
                .verify();
    }

    @DisplayName("должен отображать жанры")
    @Order(6)
    @Test
    void shouldFindAllGenresInBook() {

        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class).block();
        assertThat(expectedBook).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

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