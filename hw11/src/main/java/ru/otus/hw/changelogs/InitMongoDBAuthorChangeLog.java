package ru.otus.hw.changelogs;

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
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeUnit(id = "db-initializer-author", order = "1", author = "ypi")
@Slf4j
public class InitMongoDBAuthorChangeLog {

    private static final String COLLECTION_NAME = "author";

    private static final int AUTHOR_CNT = 4;


    @BeforeExecution
    public void dropDB(MongoDatabase mongoDatabase) {
        log.info("BeforeExecution dropDB {}", mongoDatabase.getName());
        log.info("BeforeExecution dropDB {}", mongoDatabase.listCollectionNames().first());
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        MongoCollection<Author> collection = mongoDatabase.getCollection(COLLECTION_NAME, Author.class);

        collection.drop().subscribe(subscriber);
        subscriber.await();

        SubscriberSync<Void> voidSubscriber = new MongoSubscriberSync<>();
        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(voidSubscriber);
        voidSubscriber.await();

    }

    @RollbackBeforeExecution
    public void rollbackDB(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        MongoCollection<Author> collection = mongoDatabase.getCollection(COLLECTION_NAME, Author.class);

        collection.drop().subscribe(subscriber);
        subscriber.await();
    }

    @Execution()
    public void initAuthors(AuthorRepository authorRepository) {
        log.info("initAuthors ");


        List<Author> authors = IntStream.range(1, AUTHOR_CNT)
                .mapToObj(i -> new Author("Author_" + i))
                .collect(Collectors.toList());

        authorRepository.saveAll(authors).subscribe(author -> {
            log.info("save author id {}  full name{}", author.getId(), author.getFullName());
        });
    }

    @RollbackExecution
    public void rollbackAuthors(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
        MongoCollection<Author> collection = mongoDatabase.getCollection(COLLECTION_NAME, Author.class);

        collection.drop().subscribe(subscriber);
        subscriber.await();
        log.info("rollbackAuthors");
    }

}
