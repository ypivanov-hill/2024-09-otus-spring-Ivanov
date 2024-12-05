package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeLog(order = "001")
@Slf4j
public class InitMongoDBDataChangeLog {

    private static final int AUTHOR_CNT = 41;

    private static final int BOOK_CNT = 4;

    private static final int GENRE_CNT = 7;

    private static final int COMMENT_CNT = 3;


    private final Random random = new Random();

    @ChangeSet(order = "000", id = "dropDB", author = "ypi", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "ypi", runAlways = true)
    public void initAuthors(AuthorRepository repository) {

        IntStream.range(1, AUTHOR_CNT)
                .forEach(i -> repository.save(new Author("Author_" + i)));
    }

    @ChangeSet(order = "002", id = "initGenres", author = "ypi", runAlways = true)
    public void initGenres(GenreRepository repository) {
        IntStream.range(1, GENRE_CNT)
                .forEach(i -> repository.save(new Genre("Genre_" + i)));
    }

    @ChangeSet(order = "003", id = "initBooks", author = "ypi", runAlways = true)
    public void initBooks(BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          GenreRepository genreRepository) {
        List<Author> authors = authorRepository.findAll();
        List<Genre> allGenres = genreRepository.findAll();
        IntStream.range(1, BOOK_CNT)
                .forEach(bookIndex -> {
                    int authorIndex = random.nextInt(AUTHOR_CNT - 2);
                    log.debug("Book {}, Author = Author_{}",bookIndex, authorIndex);
                    Author author = authors.get(authorIndex);
                    List<Genre> genres = new Random().ints(2, 0, GENRE_CNT - 1)
                            .distinct()
                            .mapToObj(allGenres::get)
                            .collect(Collectors.toList());

                    bookRepository.save(new Book("BookTitle_" + bookIndex, author, genres));
                });
    }

    @ChangeSet(order = "004", id = "initComments", author = "ypi", runAlways = true)
    public void initComments(CommentRepository commentRepository, BookRepository bookRepository) {
        List<Book> books = bookRepository.findAll();
        log.debug("Book {}",books.size());
        AtomicInteger index = new AtomicInteger(1);
        books.forEach(book -> {
            log.debug("Book {} comment index {}",book.getId(), index);
            commentRepository.save(new Comment("BookComment_" + index.getAndIncrement(), book));
            commentRepository.save(new Comment("BookComment_" + index.getAndIncrement(), book));
            /*List<Comment> comments = new java.util.ArrayList<>();
            IntStream.range(1, COMMENT_CNT)
                    .forEach(i -> comments.add(new Comment("BookComment_" + index.getAndIncrement(), book)));
            commentRepository.saveAll(comments);*/
        });


    }
}
