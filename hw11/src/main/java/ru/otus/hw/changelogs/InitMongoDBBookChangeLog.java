package ru.otus.hw.changelogs;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeUnit(id = "db-initializer-book", order = "3", author = "ypi")
@Slf4j
public class InitMongoDBBookChangeLog {

    private static final String COLLECTION_NAME ="book";

    private static final int AUTHOR_CNT = 4;

    private static final int BOOK_CNT = 4000;

    private static final int GENRE_CNT = 7;

    private final Random random = new Random();

    @BeforeExecution
    public void dropCollection(MongoDatabase mongoDatabase) {
        log.info("BeforeExecution dropCollection {}", mongoDatabase.getName());
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME, Book.class).drop().subscribe(subscriber);
        subscriber.await();

        SubscriberSync<Void> voidSubscriber = new MongoSubscriberSync<>();
        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @RollbackBeforeExecution
    public void rollbackDB(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        mongoDatabase.getCollection(COLLECTION_NAME, Book.class).drop().subscribe(subscriber);
        subscriber.await();
    }

    @Execution()
    public void initCollection(GenreRepository genreRepository, AuthorRepository authorRepository, BookRepository bookRepository) {
        log.info("init Book Collection ");


        List<Author> authors = authorRepository.findAll().collectList().block();
        List<Genre> allGenres = genreRepository.findAll().collectList().block();

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
                }).toList();

        bookRepository.saveAll(allBooks).subscribe(book -> {
            log.info("save book id {}  title {}", book.getId(), book.getTitle());
        });
        /*SubscriberSync<InsertManyResult> insertSubscriber = new MongoSubscriberSync<>();
        CodecRegistry pojoCodecRegistry = CodecRegistries
                .fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider
                                .builder()
                                .automatic(true)
                                .build()));

        SubscriberSync<Author> authorSubscriberSync = new MongoSubscriberSync<>();

        mongoDatabase.getCollection("author", Author.class)
                .withCodecRegistry(pojoCodecRegistry)
                .find()
                .subscribe(authorSubscriberSync);

        List<Author> authors = authorSubscriberSync.await().get().stream().toList();

        SubscriberSync<Genre> genreSubscriberSync = new MongoSubscriberSync<>();

        mongoDatabase.getCollection("genre", Genre.class)
                .withCodecRegistry(pojoCodecRegistry)
                .find()
                .subscribe(genreSubscriberSync);

        List<Genre> allGenres = genreSubscriberSync.await().get().stream().toList();


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
                }).toList();



        mongoDatabase.getCollection(COLLECTION_NAME, Book.class)
                .withCodecRegistry(pojoCodecRegistry)
                .insertMany(allBooks)
                .subscribe(insertSubscriber);
        InsertManyResult result = insertSubscriber.getFirst();
        log.info("initBook  result {}", result.getInsertedIds().size());*/

    }

    @RollbackExecution
    public void rollbackCollection(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
        mongoDatabase.getCollection(COLLECTION_NAME, Book.class).drop().subscribe(subscriber);
        subscriber.await();
        log.info("rollbackCollection");
    }

}
