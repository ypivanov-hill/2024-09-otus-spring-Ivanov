package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Data для работы с книгами ")
@SpringBootTest
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
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private BookRepository bookRepository;


    @DisplayName("должен загружать книгу")
    @Order(1)
    @Test
    void shouldReturnCorrectBookById() {
        Query query = new Query(Criteria.where("title").is(BOOK_ONE_TITLE));
        var expectedBook = mongoTemplate.findOne(query,Book.class).block();
        assertThat(expectedBook).isNotNull();

        var actualBookMono = bookRepository.findById(expectedBook.getId());

        StepVerifier
                .create(actualBookMono)
                .assertNext(actualBook -> assertThat(actualBook)
                        .isNotNull()
                        .isEqualTo(expectedBook))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен загружать список всех книг")
    @Order(2)
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBookFlux = bookRepository.findAll();

        StepVerifier
                .create(actualBookFlux)
                .expectNextCount(BOOK_COUNT)
                .expectComplete()
                .verify();
    }

   @DisplayName("должен сохранять новую книгу")
   @Order(3)
    @Test
    void shouldSaveNewBook() {
       Query query = new Query(Criteria.where("fullName").is(AUTHOR_TWO_FULL_NAME));
       var expectedAuthor = mongoTemplate.findOne(query,Author.class).block();

       query = new Query(Criteria.where("name").is(GENRE_ONE_NAME));
       var expectedGenre1 = mongoTemplate.findOne(query,Genre.class).block();
       query = new Query(Criteria.where("name").is(GENRE_TWO_NAME));
       var expectedGenre2 = mongoTemplate.findOne(query,Genre.class).block();


        var expectedBook = new Book( "BookTitle_10500", expectedAuthor,
                List.of(expectedGenre1, expectedGenre2));

        var returnedBook = bookRepository.save(expectedBook);

       StepVerifier
               .create(returnedBook)
               .assertNext(book -> assertThat(book)
                       .isNotNull()
                       .usingRecursiveComparison()
                       .ignoringExpectedNullFields()
                       .isEqualTo(expectedBook))
               .expectComplete()
               .verify();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Order(4)
    @Test
    void shouldSaveUpdatedBook() {
        Query query = new Query(Criteria.where("title").is(BOOK_TWO_TITLE));
        var expectedBook = mongoTemplate.findOne(query,Book.class).block();
        assertThat(expectedBook).isNotNull();
        expectedBook.setTitle("BookTitle_2_NEW");
        var returnedBook = bookRepository.save(expectedBook);

        StepVerifier
                .create(returnedBook)
                .assertNext(book -> assertThat(book)
                        .isNotNull()
                        .matches(book1 -> book1.getId() != null && book1.getId().equals(expectedBook.getId()))
                        .usingRecursiveComparison()
                        .ignoringExpectedNullFields()
                        .isEqualTo(expectedBook))
                .expectComplete()
                .verify();
    }

    @DisplayName("должен удалять книгу и комментарии по id ")
    @Test
    void shouldDeleteBook() {

        Query query = new Query(Criteria.where("title").is(BOOK_THREE_TITLE));
        var book = mongoTemplate.findOne(query,Book.class).block();
        assertThat(book).isNotNull();

        Query queryComments = new Query(Criteria.where("book._id").is(book.getId()));
        var comments = mongoTemplate.find(queryComments, Comment.class).collectList().block();
        assertThat(comments).isNotEmpty().hasSize(2);

        bookRepository.deleteBookById(book.getId());

        book = mongoTemplate.findOne(query,Book.class).block();

        assertThat(book).isNull();

        comments = mongoTemplate.find(queryComments, Comment.class).collectList().block();
        assertThat(comments).isEmpty();
    }
}