package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для книг должен")
@DataJpaTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_BOOK_ID = 2L;
    private static final long THIRD_BOOK_ID = 3L;

    private static final int BOOK_COUNT = 3;

    private static final String FIRST_BOOK_NAME = "BookTitle_1";
    private static final String SECOND_BOOK_NAME = "BookTitle_2";
    private static final String THIRD_BOOK_NAME = "BookTitle_3";

    private static final String NEW_BOOK_TITLE = "BookTitle_New";

    private static final long FIRST_AUTHOR_ID = 1L;
    private static final String SECOND_AUTHOR_FULL_NAME = "Author_2";

    private static final long FIRST_GENRE_ID = 1L;
    private static final long SECOND_GENRE_ID = 2L;
    private static final long FOURTH_GENRE_ID = 4L;

    @Autowired
    private BookService bookService;

    @DisplayName("возвращать книгу по её id")
    @Test
    void shouldFindById() {
        Optional<BookDto> expectedBook = bookService.findById(FIRST_BOOK_ID);
        assertThat(expectedBook).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("title", FIRST_BOOK_NAME);
    }

    @DisplayName("находить все все книги")
    @Test
    void shouldFindAllBooks() {
        List<BookDto> expectedBooks = bookService.findAll();
        assertThat(expectedBooks).hasSize(BOOK_COUNT)
                  .anyMatch(book -> SECOND_BOOK_NAME.equals(book.getTitle()));
    }

    @DisplayName("добавлять новые книги")
    @Test
    void shouldInsertBook() {

        List<BookDto> expectedBooks = bookService.findAll();

        int beforeInsetCount = expectedBooks.size();

        BookDto returnedBook = bookService.insert(NEW_BOOK_TITLE, FIRST_AUTHOR_ID, Set.of(FIRST_GENRE_ID, SECOND_GENRE_ID));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

       assertThat(returnedBook).isNotNull()
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("id", FIRST_AUTHOR_ID);

        assertThat(returnedBook.getGenres())
                .hasSize(2)
                .anyMatch(genre -> genre.getId() == FIRST_GENRE_ID);

        expectedBooks = bookService.findAll();
        assertThat(expectedBooks).isNotEmpty()
                .hasSize(beforeInsetCount + 1);
    }

    @DisplayName("изменять книги")
    @Test
    void shouldUpdateBook() {
        Optional<BookDto> expectedBook = bookService.findById(THIRD_BOOK_ID);
        assertThat(expectedBook).isNotEmpty();
        assertThat(expectedBook.get()).isNotNull()
                .hasFieldOrPropertyWithValue("title", THIRD_BOOK_NAME);

        BookDto returnedBook = bookService.update(THIRD_BOOK_ID, NEW_BOOK_TITLE, FIRST_AUTHOR_ID, Set.of(FIRST_GENRE_ID, FOURTH_GENRE_ID));
        assertThat(returnedBook).isNotNull()
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("id", FIRST_AUTHOR_ID);

        assertThat(returnedBook.getGenres())
                .hasSize(2)
                .anyMatch(genre -> genre.getId() == FIRST_GENRE_ID);
    }

    @DisplayName("удалять книги по id")
    @Test
    void shouldDeleteBookById() {
        bookService.deleteById(FIRST_BOOK_ID);
        Optional<BookDto> expectedBook = bookService.findById(FIRST_BOOK_ID);
        assertThat(expectedBook).isEmpty();
    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBook() {
        Optional<BookDto> returnedBook = bookService.findById(SECOND_BOOK_ID);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getAuthor()).isNotNull()
                .hasFieldOrPropertyWithValue("fullName", SECOND_AUTHOR_FULL_NAME);
    }

    @DisplayName("должен отображать жанры")
    @Test
    void shouldFindAllGenresInBook() {
        Optional<BookDto> returnedBook = bookService.findById(SECOND_BOOK_ID);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getGenres())
                .isNotNull()
                .hasSize(2)
                .anyMatch(genre -> genre.getId() == FOURTH_GENRE_ID);
    }

    @DisplayName("не должен отображать комментарии")
    @Test
    void shouldFindCommentsInBook() {
        Optional<BookDto> returnedBook = bookService.findById(SECOND_BOOK_ID);

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getComments())
                .isNotNull()
                .hasSize(0);
    }
}