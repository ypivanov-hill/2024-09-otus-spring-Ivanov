package ru.otus.hw.repositories;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
//@Transactional(propagation = Propagation.REQUIRED)
class JpaBookRepositoryTest {

    private static final int BOOK_COUNT = 3;

    private static final int BOOK_TWO_ID = 2;

    private static final int AUTHOR_TWO_ID = 2;

    private static final int GENRE_ONE_ID = 1;
    private static final int GENRE_TWO_ID = 2;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookRepository repositoryJpa;


    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        var expectedBook =em.find(Book.class, BOOK_TWO_ID);
        var actualBook = repositoryJpa.findById(BOOK_TWO_ID);
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repositoryJpa.findAll();
        assertThat(actualBooks).hasSize(BOOK_COUNT);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedAuthor = em.find(Author.class,AUTHOR_TWO_ID);
        var expectedGenre1 = em.find(Genre.class,GENRE_ONE_ID);
        var expectedGenre2 = em.find(Genre.class,GENRE_TWO_ID);

        var expectedBook = new Book(0, "BookTitle_10500", expectedAuthor,
                List.of(expectedGenre1, expectedGenre2), List.of());
        var returnedBook = repositoryJpa.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(repositoryJpa.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = em.find(Book.class,BOOK_TWO_ID);
        Hibernate.initialize(expectedBook.getGenres());
        Hibernate.initialize(expectedBook.getComments());
        em.detach(expectedBook);
        expectedBook.setTitle("BookTitle_2_NEW");

        assertThat(repositoryJpa.findById(BOOK_TWO_ID))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = repositoryJpa.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(repositoryJpa.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        repositoryJpa.deleteById(BOOK_TWO_ID);
        var expectedBook = em.find(Book.class,BOOK_TWO_ID);
        assertThat(expectedBook).isNull();
    }
}