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
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ChangeUnit(id = "db-initializer-genre", order = "2", author = "ypi")
@Slf4j
public class InitMongoDBGenreChangeLog {

    private static final String COLLECTION_NAME ="genre";

    private static final int GENRE_CNT = 7;

    @BeforeExecution
    public void dropCollection(MongoDatabase mongoDatabase) {
        log.info("BeforeExecution dropCollection {}", mongoDatabase.getName());
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();

        MongoCollection<Genre> collection = mongoDatabase.getCollection(COLLECTION_NAME, Genre.class);

        collection.drop().subscribe(subscriber);
        subscriber.await();

        SubscriberSync<Void> voidSubscriber = new MongoSubscriberSync<>();
        mongoDatabase.createCollection(COLLECTION_NAME).subscribe(voidSubscriber);
        voidSubscriber.await();
    }

    @RollbackBeforeExecution
    public void rollbackDB(MongoDatabase database) { }

    @Execution()
    public void initCollection(MongoDatabase mongoDatabase, GenreRepository genreRepository) {
        log.info("initGenre ");



        List<Genre> genres = IntStream.range(1, GENRE_CNT)
                .mapToObj(i -> new Genre("Genre_" + i))
                .collect(Collectors.toList());

        genreRepository.saveAll(genres)
                .subscribe(genre -> log.debug("save Genre id {} name {}", genre.getId(), genre.getName())
                );

        /*SubscriberSync<InsertManyResult> subscriber = new MongoSubscriberSync<>();
        CodecRegistry pojoCodecRegistry = CodecRegistries
                .fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider
                                .builder()
                                .automatic(true)
                                .build()));

        mongoDatabase.getCollection(COLLECTION_NAME, Genre.class)
                .withCodecRegistry(pojoCodecRegistry)
                .insertMany(genres)
                .subscribe(subscriber);
        InsertManyResult result = subscriber.getFirst();
        log.info("init  result {}", result.getInsertedIds().size());*/

    }

    @RollbackExecution
    public void rollbackCollection(MongoDatabase mongoDatabase) {
        SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
        mongoDatabase.getCollection(COLLECTION_NAME, Genre.class).drop().subscribe(subscriber);
        subscriber.await();
        log.info("rollbackCollection");
    }

}
