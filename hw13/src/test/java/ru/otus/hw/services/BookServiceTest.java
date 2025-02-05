package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookCompliteDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Сервис для книг должен")
@DataJpaTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        AclServiceWrapperServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class BookServiceTest {


    private static final String NEW_BOOK_TITLE = "BookTitle_New";

    private List<Author> authors = List.of(new Author(1, "Author 1 FullName"),
            new Author(2, "Author 2 FullName"));

    private List<Genre> genres = List.of(new Genre(1, "Genre1"),
            new Genre(2, "Genre2"),
            new Genre(3, "Genre3"),
            new Genre(4, "Genre4"));

    private List<Book> books = List.of(new Book(1, "TestBook1", authors.get(0), List.of(genres.get(0), genres.get(1))),
            new Book(2, "TestBook2", authors.get(1), List.of(genres.get(2), genres.get(3))));

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

    @MockBean
    private AclServiceWrapperService aclServiceWrapperService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JdbcMutableAclService mutableAclService;

    @DisplayName("возвращать книгу по её id")
    @Test
    void shouldFindById() {
        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isNotEmpty().get()
                .hasFieldOrPropertyWithValue("title", expectedBook.getTitle());
    }


    @DisplayName("находить все все книги")
    @Test
    void shouldFindAllBooks() {

        when(bookRepository.findAll()).thenReturn(books);

        int booksCount = books.size();

        List<BookCompliteDto> returnedBooks = bookService.findAll();

        assertThat(returnedBooks).hasSize(booksCount)
                .anyMatch(book -> books.get(0).getTitle().equals(book.getTitle()));
    }

    @DisplayName("добавлять новые книги")
    @Test
    void shouldInsertBook() {

        Book expectedBooks = books.get(0);

        Book expectedBook = books.get(0);
        expectedBook.setTitle(NEW_BOOK_TITLE);

        when(bookRepository.findById(expectedBook.getId()))
                .thenReturn(Optional.of(expectedBook));

        Book newBook = new Book(0,
                NEW_BOOK_TITLE,
                expectedBook.getAuthor(),
                expectedBook.getGenres());

        when(bookRepository.save(newBook)).thenReturn(expectedBook);

        when(authorRepository.findById(newBook.getAuthor().getId()))
                .thenReturn(Optional.of(newBook.getAuthor()));

        when(genreRepository.findAllByIdIn(any(Set.class)))
                        .thenReturn(newBook.getGenres());

        BookDto returnedBook = bookService.insert(NEW_BOOK_TITLE, expectedBooks.getAuthor().getId(),
                expectedBooks.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthorId()).isNotNull()
                .isEqualTo(expectedBooks.getAuthor().getId());

        assertThat(returnedBook.getGenreIds())
                .hasSize(expectedBooks.getGenres().size())
                .anyMatch(genre -> genre.equals(expectedBooks.getGenres().get(0).getId()));
    }

    @DisplayName("изменять книги")
    @Test
    void shouldUpdateBook() {
        Book expectedBooks = books.get(0);

        Book expectedBook = books.get(0);
        expectedBook.setTitle(NEW_BOOK_TITLE);

        when(bookRepository.findById(expectedBook.getId()))
                .thenReturn(Optional.of(expectedBook));

        Book newBook = expectedBook;

        newBook.setTitle(NEW_BOOK_TITLE);


        when(bookRepository.save(newBook)).thenReturn(expectedBook);

        when(authorRepository.findById(newBook.getAuthor().getId()))
                .thenReturn(Optional.of(newBook.getAuthor()));

        when(genreRepository.findAllByIdIn(expectedBook.
                getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()))).
                thenReturn(expectedBook.
                        getGenres());

        BookDto returnedBook = bookService.update(newBook.getId(),
                newBook.getTitle(),
                newBook.getAuthor().getId(),
                expectedBooks.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .hasFieldOrPropertyWithValue("title", NEW_BOOK_TITLE);

        assertThat(returnedBook.getAuthorId()).isNotNull()
                .isEqualTo(expectedBooks.getAuthor().getId());

        assertThat(returnedBook.getGenreIds())
                .hasSize(expectedBooks.getGenres().size())
                .anyMatch(genre -> genre.equals(expectedBooks.getGenres().get(0).getId()));
    }

    @DisplayName("удалять книги и все комментарии по id")
    @Test
    void shouldDeleteBookById() {

        Book expectedBook = books.get(0);

        bookService.deleteById(expectedBook.getId());

        verify(bookRepository, times(1)).deleteById(expectedBook.getId());

    }

    @DisplayName("должен отображать автора")
    @Test
    void shouldFindAuthorInBook() {
        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));

        long expectedAuthorId = expectedBook.getAuthor().getId();

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getAuthorId()).isNotNull()
                .isEqualTo(expectedAuthorId);
    }

    @DisplayName("должен отображать жанры")
    @Test
    void shouldFindAllGenresInBook() {

        Book expectedBook = books.get(0);
        when(bookRepository.findById(expectedBook.getId())).thenReturn(Optional.of(expectedBook));

        int expectedGenresCount = expectedBook.getGenres().size();
        long expectedGenreId = expectedBook.getGenres().get(0).getId();

        Optional<BookDto> returnedBook = bookService.findById(expectedBook.getId());

        assertThat(returnedBook).isNotEmpty();

        assertThat(returnedBook.get().getGenreIds())
                .isNotNull()
                .hasSize(expectedGenresCount)
                .anyMatch(genre -> genre.equals(expectedGenreId));
    }
}