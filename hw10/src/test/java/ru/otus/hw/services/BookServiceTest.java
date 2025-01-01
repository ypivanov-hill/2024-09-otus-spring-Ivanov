package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для книг должен")
@DataMongoTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceTest {

    private static final int BOOK_COUNT = 3;

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
    private MongoOperations mongoTemplate;

    @Autowired
    private GenreConverter genreConverter;

    @DisplayName("возвращать книгу по её id")
    @Order(1)
    @Test
    void shouldFindById() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());
        assertThat(returnedBook).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME);
    }


    @DisplayName("находить все все книги")
    @Order(2)
    @Test
    void shouldFindAllBooks() {

        var expectedBooks = mongoTemplate.findAll(Book.class);
        List<BookDto> returnedBooks = bookService.findAll();
        assertThat(returnedBooks)
                .hasSize(expectedBooks.size())
                .anyMatch(book -> FIRST_BOOK_NAME.equals(book.getTitle()));
    }

    @DisplayName("добавлять новые книги")
    @Order(3)
    @Test
    void shouldInsertBook() {

        List<Book> expectedBooks = mongoTemplate.findAll(Book.class);

        Query query = new Query(Criteria.where("fullName").is(FIRST_AUTHOR_FULL_NAME));
        var expectedAuthor = mongoTemplate.findOne(query, Author.class);
        assertThat(expectedAuthor).isNotNull();

        query = new Query(Criteria.where("name").is(FIRST_GENRE_NAME));
        var expectedGenre = genreConverter.genreToDto(mongoTemplate.findOne(query, Genre.class));
        assertThat(expectedGenre).isNotNull();

        int beforeInsetCount = expectedBooks.size();

        BookDto returnedBook = bookService.insert(NEW_BOOK_TITLE,
                new AuthorDto(expectedAuthor.getId(),
                        expectedAuthor.getFullName()),
                Set.of(expectedGenre));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthor().getId()).isNotNull()
                .hasToString(expectedAuthor.getId());

        assertThat(returnedBook.getGenres())
                .hasSize(1)
                .anyMatch(genre -> genre.getId().equals(expectedGenre.getId()));

        expectedBooks = mongoTemplate.findAll(Book.class);
        assertThat(expectedBooks).isNotEmpty()
                .hasSize(beforeInsetCount + 1);
    }

    @DisplayName("изменять книги")
    @Order(4)
    @Test
    void shouldUpdateBook() {
        Query query = new Query(Criteria.where("title").is(THIRD_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        query = new Query(Criteria.where("fullName").is(FIRST_AUTHOR_FULL_NAME));
        var expectedAuthor = mongoTemplate.findOne(query, Author.class);
        assertThat(expectedAuthor).isNotNull();

        query = new Query(Criteria.where("name").is(FIRST_GENRE_NAME));
        var expectedGenre = genreConverter.genreToDto(mongoTemplate.findOne(query, Genre.class));
        assertThat(expectedGenre).isNotNull();

        BookDto returnedBook = bookService.update(expectedBook.getId(),
                NEW_BOOK_TITLE,
                new AuthorDto(expectedAuthor.getId(),
                        expectedAuthor.getFullName()), Set.of(expectedGenre));
        assertThat(returnedBook).isNotNull()
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);


        assertThat(returnedBook.getAuthor().getId()).isNotNull()
                .hasToString(expectedAuthor.getId());

        assertThat(returnedBook.getGenres())
                .hasSize(1)
                .anyMatch(genre -> genre.getId().equals(expectedGenre.getId()));
    }

    @DisplayName("удалять книги и все комментарии по id")
    @Order(7)
    @Test
    void shouldDeleteBookById() {

        Query query = new Query(Criteria.where("title").is(SECOND_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        bookService.deleteById(expectedBook.getId());

        Query queryComments = new Query(Criteria.where("book._id").is(expectedBook.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class);
        assertThat(comments).isEmpty();

    }

    @DisplayName("должен отображать автора")
    @Order(5)
    @Test
    void shouldFindAuthorInBook() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull().extracting(Book::getAuthor).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

        String expectedAuthorId = expectedBook.getAuthor().getId();


        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getAuthor().getId()).isNotNull()
                .hasToString(expectedAuthorId);
    }

    @DisplayName("должен отображать жанры")
    @Order(6)
    @Test
    void shouldFindAllGenresInBook() {

        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

        int expectedGenresCount = expectedBook.getGenres().size();
        String expectedGenreId = expectedBook.getGenres().get(0).getId();

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getGenres())
                .isNotNull()
                .hasSize(expectedGenresCount)
                .anyMatch(genre -> genre.getId().equals(expectedGenreId));
    }
}