package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.BookNew;
import ru.otus.hw.models.in.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ru.otus.hw.changelogs.InitMongoDBDataChangeLog.AUTHOR_CNT;
import static ru.otus.hw.changelogs.InitMongoDBDataChangeLog.GENRE_CNT;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final Random random = new Random();

    @Override
    public Book getBook(BookNew bookNew) {
        log.error("getBookNew book {}", bookNew.getTitle());
        Book book = new Book();

        book.setTitle(bookNew.getTitle());
        Author author = authorRepository
                .findByFullName(bookNew.getAuthorName())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        List<Genre> genres = bookNew.getGenreNames().stream().map(g -> {
            return genreRepository.findFirstByName(g)
                    .orElseThrow(() -> new RuntimeException("Genre not found"));
        }).toList();

        book.setAuthor(author);

        book.setGenres(genres);

        return book;
    }

    public void insertBookNew(Message<?> message) {
        List<Book> books = (List<Book>) message.getPayload();
        log.error("insertBookNew book {}", books.size());
        bookRepository.saveAll(books);
    }

    public List<BookNew> addNewBooks(int count) {
        List<Author> authors = authorRepository.findAll();
        List<Genre> allGenres = genreRepository.findAll();
        long oldBooksCnt = bookRepository.count();
        List<Book> allBooks = LongStream.range(oldBooksCnt + 1, oldBooksCnt + count + 1)
                .mapToObj(bookIndex -> {
                    int authorIndex = random.nextInt(AUTHOR_CNT - 2);
                    log.debug("Book {}, Author = Author_{}",bookIndex, authorIndex);
                    Author author = authors.get(authorIndex);
                    List<Genre> genres = new Random().ints(2, 0, GENRE_CNT - 1)
                            .distinct()
                            .mapToObj(allGenres::get)
                            .collect(Collectors.toList());

                    return new Book("BookTitle_" + bookIndex, author, genres);
                }).toList();
        return allBooks.stream()
                .map(book ->
                        new BookNew(book.getTitle(),
                                book.getAuthor().getFullName(),
                                book.getGenres().stream().map(Genre::getName)
                                        .toList()))
                .toList();
    }

    @Override
    public boolean existsByTitle(String title) {
        return bookRepository.existsByTitle(title);
    }
}
