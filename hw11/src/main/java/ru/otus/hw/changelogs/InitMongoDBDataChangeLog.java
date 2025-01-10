package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
//import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import io.mongock.runner.core.executor.system.changes.SystemChangeUnit00001;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeUnit(id = "db-initializer", order = "1", author = "ypi")
@Slf4j
public class InitMongoDBDataChangeLog {

    private static final int AUTHOR_CNT = 4;

    private static final int BOOK_CNT = 4;

    private static final int GENRE_CNT = 7;

    private static final int COMMENT_CNT = 3;


    private final Random random = new Random();

    //@ChangeSet(order = "000", id = "dropDB", author = "ypi", runAlways = true)
   @BeforeExecution
    public void dropDB(MongoDatabase database) {
        log.info("BeforeExecution dropDB {}" ,database.getName() );
       log.info("BeforeExecution dropDB {}" ,database.listCollectionNames().first());
        database.drop();
       database.createCollection("Authors");
       var a = database.getCollection("Authors");
       var b= a.find().first();
       log.info("BeforeExecution dropDB {}" ,a.find().first());
    }

    @RollbackBeforeExecution
    public void rollbackDB(MongoDatabase database) {
        log.info("RollbackBeforeExecution dropDB");
        database.drop();
    }

    @Execution()//@ChangeSet(order = "001", id = "initAuthors", author = "ypi", runAlways = true)
    public void initAuthors(/*ClientSession clientSession,*/ MongoDatabase mongoDatabase, AuthorRepository repository) {
        log.info("initAuthors " );
        List<Author> authors = IntStream.range(1, AUTHOR_CNT)
                 .mapToObj(i -> new Author("Author_" + i))
                 .collect(Collectors.toList());
        log.info("initAuthors  {}" ,authors.size());
        repository.saveAll(authors);

        SubscriberSync<InsertManyResult> subscriber = new MongoSubscriberSync<>();
        mongoDatabase.getCollection( "Authors", Author.class).withDocumentClass(Author.class)
                .insertMany(/*clientSession,*/authors)
                .subscribe(subscriber);
        InsertManyResult result = subscriber.getFirst();
        log.info("initAuthors  result {}" ,result.getInsertedIds().size());

    }

    @RollbackExecution//@ChangeSet(order = "001", id = "initAuthors", author = "ypi", runAlways = true)
    public void rollbackAuthors(AuthorRepository repository) {
        repository.findAll().subscribe(repository::delete);
        //repository.saveAll(authors);
    }



  /*  @ChangeSet(order = "002", id = "initGenres", author = "ypi", runAlways = true)
    public void initGenres(GenreRepository repository) {
        List<Genre> genres = IntStream.range(1, GENRE_CNT)
                .mapToObj(i -> new Genre("Genre_" + i))
                .collect(Collectors.toList());
        repository.saveAll(genres);
    }*/

    /*@ChangeSet(order = "003", id = "initBooks", author = "ypi", runAlways = true)
    public void initBooks(BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          GenreRepository genreRepository) {
        Flux<Author> authors = authorRepository.findAll();
        Flux<Genre> allGenres = genreRepository.findAll();
        List<Book> allBooks = IntStream.range(1, BOOK_CNT)
                .mapToObj(bookIndex -> {
                    int authorIndex = random.nextInt(AUTHOR_CNT - 2);
                    log.debug("Book {}, Author = Author_{}",bookIndex, authorIndex);
                    Author author = authors.get(authorIndex);
                    List<Genre> genres = new Random().ints(2, 0, GENRE_CNT - 1)
                            .distinct()
                            .mapToObj(allGenres::get)
                            .collect(Collectors.toList());

                    return new Book("BookTitle_" + bookIndex, author, genres);
                }).collect(Collectors.toList());
        bookRepository.saveAll(allBooks);
    }*/

   /* @ChangeSet(order = "003", id = "initBooks", author = "ypi", runAlways = true)
    public void initBooks(BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          GenreRepository genreRepository) {
        Flux<Author> authors = authorRepository.findAll();
        Flux<Genre> allGenres = genreRepository.findAll();
        List<Book> allBooks = IntStream.range(1, BOOK_CNT)
                .mapToObj(bookIndex -> {
                    int authorIndex = random.nextInt(AUTHOR_CNT - 2);
                    log.debug("Book {}, Author = Author_{}",bookIndex, authorIndex);
                    Mono<Author> author = authors.take(authorIndex).single();
                    Flux<Genre> genres = allGenres.take(authorIndex).flatMap(Mono::just);
                  /*  new Random().ints(2, 0, GENRE_CNT - 1)
                            .flatMap(i - > {});*/
                    /*List<Genre> genres = new Random().ints(2, 0, GENRE_CNT - 1)
                            .distinct()
                            .mapToObj(allGenres::get)
                            .collect(Collectors.toList());*/

         /*           return new Book("BookTitle_" + bookIndex, author, allGenres);// author.zipWith(genres, (authorx, genresx) -> new Book("BookTitle_" + bookIndex, authorx, genresx));
                }).collect(Collectors.toList());
        bookRepository.saveAll(allBooks);
    }

  /*  @ChangeSet(order = "004", id = "initComments", author = "ypi", runAlways = true)
    public void initComments(CommentRepository commentRepository,
                             BookRepository bookRepository,
                             MongockTemplate mongoTemplate) {
        //List<Book> books = bookRepository.findAll();
        List<Book> books = mongoTemplate.findAll(Book.class);
        log.debug("Book {}",books.size());
        List<Comment> comments = new java.util.ArrayList<>();
        AtomicInteger index = new AtomicInteger(1);

        for (Book book : books) {
            log.debug("Book {} comment index {}",book.getId(), index);
            comments.addAll(IntStream.range(1, COMMENT_CNT)
                    .mapToObj(i -> new Comment("BookComment_" + index.getAndIncrement(), book))
                    .toList());
        }
        mongoTemplate.insertAll(comments);
        //commentRepository.saveAll(comments);
    }*/
}
