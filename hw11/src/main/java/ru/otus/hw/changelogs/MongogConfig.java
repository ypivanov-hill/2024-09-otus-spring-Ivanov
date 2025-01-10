package ru.otus.hw.changelogs;


import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongogConfig {

    @Bean
    public MongoReactiveDriver getConnectionDriver(MongoClient mongoClient) {


        // For mongodb-sync-v4-driver
        MongoReactiveDriver driver = MongoReactiveDriver.withDefaultLock(mongoClient, "hw08Mongo");
// For mongodb-v3-driver
//MongoCore3Driver driver = MongoCore3Driver.withDefaultLock(mongoClient, databaseName);
        driver.setWriteConcern(WriteConcern.MAJORITY.withJournal(true).

                withWTimeout(1000, TimeUnit.MILLISECONDS));

        driver.setReadConcern(ReadConcern.MAJORITY);
        driver.setReadPreference(ReadPreference.primary());
        driver.disableTransaction();
        return driver;
    }

    @Bean
    public Publisher<ClientSession> clientSession(MongoClient mongoClient) {

    ClientSessionOptions sessionOptions = ClientSessionOptions.builder()
            .causallyConsistent(true)
            .build();

        return mongoClient.startSession(sessionOptions);
}
}
