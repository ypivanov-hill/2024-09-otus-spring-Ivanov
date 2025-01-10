package ru.otus.hw.changelogs;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
//import io.mongock.runner.standalone.MongockStandalone;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class RunnerBuilderProviderImpl /*implements RunnerBuilderProvider */{
    public final static String MONGODB_CONNECTION_STRING = "mongodb://localhost:27017/";
    public final static String MONGODB_MAIN_DB_NAME = "hw08Mongo";

   /* @Override
    public RunnerBuilder getBuilder() {
        //Object MongockEventListener;
        return MongockStandalone.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(getMainMongoClient(), MONGODB_MAIN_DB_NAME))
                .addMigrationScanPackage("ru.otus.hw.changelogs")
                .setMigrationStartedListener(MongockEventListener::onStart)
                .setMigrationSuccessListener(MongockEventListener::onSuccess)
                .setMigrationFailureListener(MongockEventListener::onFail)
                .setTrackIgnored(true)
                .setTransactional(true);
    }

    /**
     * Main MongoClient for Mongock to work.
     */
    /*private static MongoClient getMainMongoClient() {
        return buildMongoClientWithCodecs(MONGODB_CONNECTION_STRING);
    }


    /**
     * Helper to create MongoClients customized including Codecs
     */
   /* private static MongoClient buildMongoClientWithCodecs(String connectionString) {

        /*CodecRegistry codecRegistry = fromRegistries(fromCodecs(new ZonedDateTimeCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),

        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(connectionString));
        builder.retryWrites(Boolean.FALSE);
        //builder.tr
               // builder.codecRegistry(codecRegistry);
        MongoClientSettings build = builder.build();
        return MongoClients.create(build);
    }            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        */
}
