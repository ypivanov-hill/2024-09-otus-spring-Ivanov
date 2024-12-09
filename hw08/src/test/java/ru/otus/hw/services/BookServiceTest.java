package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для книг должен")
@DataMongoTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class})
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

    @DisplayName("возвращать книгу по её id")
    @Test
    void shouldFindById() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());
        assertThat(returnedBook).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME);
    }

    @DisplayName("возвращать книгу по её названию")
    @Test
    void shouldFindByTitle() {
        Optional<BookDto> returnedBook = bookService.findByTitleIgnoreCase(FIRST_BOOK_NAME);
        assertThat(returnedBook).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME);
    }

    @DisplayName("находить все все книги")
    @Test
    void shouldFindAllBooks() {
        List<BookDto> expectedBooks = bookService.findAll();
        assertThat(expectedBooks)
                .hasSize(BOOK_COUNT)
                .anyMatch(book -> FIRST_BOOK_NAME.equals(book.getTitle()));
    }

    @DisplayName("добавлять новые книги")
    @Test
    void shouldInsertBook() {

        List<Book> expectedBooks = mongoTemplate.findAll(Book.class);

        Query query = new Query(Criteria.where("fullName").is(FIRST_AUTHOR_FULL_NAME));
        var expectedAuthor = mongoTemplate.findOne(query, Author.class);
        assertThat(expectedAuthor).isNotNull();

        int beforeInsetCount = expectedBooks.size();

        BookDto returnedBook = bookService.insert(NEW_BOOK_TITLE, FIRST_AUTHOR_FULL_NAME, Set.of(FIRST_GENRE_NAME, SECOND_GENRE_NAME));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("id", expectedAuthor.getId());

        assertThat(returnedBook.getGenres())
                .hasSize(2)
                .anyMatch(genre -> genre.getName().equals(FIRST_GENRE_NAME));

        expectedBooks = mongoTemplate.findAll(Book.class);
        assertThat(expectedBooks).isNotEmpty()
                .hasSize(beforeInsetCount + 1);
    }

    @DisplayName("изменять книги")
    @Test
    void shouldUpdateBook() {
        Query query = new Query(Criteria.where("title").is(THIRD_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        BookDto returnedBook = bookService.update(expectedBook.getId(), NEW_BOOK_TITLE, FIRST_AUTHOR_FULL_NAME, Set.of(FIRST_GENRE_NAME, SECOND_GENRE_NAME));
        assertThat(returnedBook).isNotNull()
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("fullName", FIRST_AUTHOR_FULL_NAME);

        assertThat(returnedBook.getGenres())
                .hasSize(2)
                .anyMatch(genre -> genre.getName().equals(FIRST_GENRE_NAME));
    }

    @DisplayName("удалять книги и все комментарии по id")
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
    @Test
    void shouldFindAuthorInBook() {
        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull().extracting(Book::getAuthor).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

        String expectedAuthorFullName = expectedBook.getAuthor().getFullName();

        Optional<BookDto> returnedBook = bookService.findByTitleIgnoreCase(FIRST_BOOK_NAME);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("fullName", expectedAuthorFullName);
    }

    @DisplayName("должен отображать жанры")
    @Test
    void shouldFindAllGenresInBook() {

        Query query = new Query(Criteria.where("title").is(FIRST_BOOK_NAME));
        var expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();

        assertThat(expectedBook.getGenres()).isNotEmpty().first().isNotNull();

        int expectedGenresCount = expectedBook.getGenres().size();
        String expectedGenreId = expectedBook.getGenres().get(0).getId();

        Optional<BookDto> returnedBook = bookService.findByTitleIgnoreCase(FIRST_BOOK_NAME);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getGenres())
                .isNotNull()
                .hasSize(expectedGenresCount)
                .anyMatch(genre -> genre.getId().equals(expectedGenreId));
    }

    @DisplayName("не должен отображать комментарии")
    @Test
    void shouldFindCommentsInBook() {
        Optional<BookDto> returnedBook = bookService.findByTitleIgnoreCase(FIRST_BOOK_NAME);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getComments())
                .isNotNull()
                .hasSize(0);
    }
}