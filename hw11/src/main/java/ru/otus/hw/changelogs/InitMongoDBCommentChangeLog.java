package ru.otus.hw.changelogs;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeUnit(id = "db-initializer-comment", order = "4", author = "ypi")
@Slf4j
public class InitMongoDBCommentChangeLog {

    private static final String COLLECTION_NAME ="comment";

    private static final int COMMENT_CNT = 3;

    @BeforeExecution
    public void dropCollection(MongoDatabase mongoDatabase) {
        log.info("BeforeExecution dropCollection {}", mongoDatabase.getName());
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        MongoCollection<Comment> collection = mongoDatabase.getCollection(COLLECTION_NAME, Comment.class);

        collection.drop().subscribe(subscriber);
        subscriber.await();

        SubscriberSync<Void> voidSubscriber = new MongoSubscriberSync<>();
        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @RollbackBeforeExecution
    public void rollbackDB(MongoDatabase database) { }

    @Execution()
    public void initCollection(MongoDatabase mongoDatabase, BookRepository bookRepository, CommentRepository commentRepository) {
        log.info("init Comment ");


       /* CodecRegistry pojoCodecRegistry =
                CodecRegistries
                .fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider
                                .builder()
                                .automatic(true)
                                .build()));
        SubscriberSync<Book> booksSubscriberSync = new MongoSubscriberSync<>();

        mongoDatabase.getCollection("book", Book.class)
                .withCodecRegistry(pojoCodecRegistry)
                .find()
                .subscribe(booksSubscriberSync);

        List<Book> books = booksSubscriberSync
                .get()
                .stream()
                .peek(book -> {
                    log.debug("Book id {} Title {}", book.getId(), book.getTitle());
                    AtomicInteger index = new AtomicInteger(1);
                    List<Comment> comments = new java.util.ArrayList<>(IntStream.range(1, COMMENT_CNT)
                            .mapToObj(i -> new Comment("BookComment_" + index.getAndIncrement(), book))
                            .toList());
                    SubscriberSync<InsertManyResult> subscriber = new MongoSubscriberSync<>();

                    mongoDatabase.getCollection(COLLECTION_NAME, Comment.class)
                            .withCodecRegistry(pojoCodecRegistry)
                            .insertMany(comments)
                            .subscribe(subscriber);
                    InsertManyResult result = subscriber.getFirst();
                    log.info("init {} result comment of {}", book.getTitle(), result.getInsertedIds().size());
                })
                .toList();*/
        try {
            CountDownLatch cdl = new CountDownLatch(1);
            bookRepository.findAll()
                    .doOnTerminate(cdl::countDown)
                    .subscribe(book -> {
                log.debug("Book id {} Title {}", book.getId(), book.getTitle());
                AtomicInteger index = new AtomicInteger(1);
                List<Comment> comments = new java.util.ArrayList<>(IntStream.range(1, COMMENT_CNT)
                        .mapToObj(i -> new Comment("BookComment_" + index.getAndIncrement(), book))
                        .toList());
                commentRepository.saveAll(comments).subscribe(comment -> log.info("save comment id {}  full name{}", comment.getId(), comment.getText()));
            });
            cdl.await(100L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("InterruptedException {} ", e.getMessage());
        }

    }

    @RollbackExecution
    public void rollbackCollection(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
        mongoDatabase.getCollection(COLLECTION_NAME, Genre.class).drop().subscribe(subscriber);
        subscriber.await();
        log.info("rollbackCollection");
    }

}
