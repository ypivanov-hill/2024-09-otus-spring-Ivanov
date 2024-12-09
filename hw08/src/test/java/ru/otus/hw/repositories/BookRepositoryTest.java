package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Data для работы с книгами ")
@DataMongoTest
@EnableConfigurationProperties
class BookRepositoryTest {

    private static final int BOOK_COUNT = 3;

    private static final String BOOK_ONE_TITLE = "BookTitle_1";
    private static final String BOOK_TWO_TITLE = "BookTitle_2";
    private static final String BOOK_THREE_TITLE = "BookTitle_3";

    private static final String AUTHOR_TWO_FULL_NAME = "Author_2";

    private static final String GENRE_ONE_NAME = "Genre_1";
    private static final String GENRE_TWO_NAME = "Genre_2";

    @Autowired
    private MongoOperations mongoTemplate;

    @Autowired
    private BookRepository bookRepository;


    @DisplayName("должен загружать книгу по названию")
    @Test
    void shouldReturnCorrectBookById() {
        Query query = new Query(Criteria.where("title").is(BOOK_ONE_TITLE));
        var expectedBook = mongoTemplate.findOne(query,Book.class);
        var actualBook = bookRepository.findByTitleIgnoreCase(BOOK_ONE_TITLE);
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        assertThat(actualBooks).hasSize(BOOK_COUNT);
        actualBooks.forEach(System.out::println);
    }

   @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
       Query query = new Query(Criteria.where("fullName").is(AUTHOR_TWO_FULL_NAME));
       var expectedAuthor = mongoTemplate.findOne(query,Author.class);

       query = new Query(Criteria.where("name").is(GENRE_ONE_NAME));
       var expectedGenre1 = mongoTemplate.findOne(query,Genre.class);
       query = new Query(Criteria.where("name").is(GENRE_TWO_NAME));
       var expectedGenre2 = mongoTemplate.findOne(query,Genre.class);


        var expectedBook = new Book( "BookTitle_10500", expectedAuthor,
                List.of(expectedGenre1, expectedGenre2));

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(mongoTemplate.findOne(new Query(Criteria.where("id").is(returnedBook.getId())), Book.class))
                .isNotNull()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Query query = new Query(Criteria.where("title").is(BOOK_TWO_TITLE));
        var expectedBook = mongoTemplate.findOne(query,Book.class);
        assertThat(expectedBook).isNotNull();
        expectedBook.setTitle("BookTitle_2_NEW");
        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null && book.getId().equals(expectedBook.getId()))
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен удалять книгу и комментарии по названию ")
    @Test
    void shouldDeleteBook() {

        Query query = new Query(Criteria.where("title").is(BOOK_THREE_TITLE));
        var book = mongoTemplate.findOne(query,Book.class);
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class);
        assertThat(comments).isNotEmpty().hasSize(2);

        bookRepository.deleteBookByTitle(BOOK_THREE_TITLE);

        book = mongoTemplate.findOne(query,Book.class);

        assertThat(book).isNull();

        comments = mongoTemplate.find(queryComments, Comment.class);
        assertThat(comments).isEmpty();
    }
}