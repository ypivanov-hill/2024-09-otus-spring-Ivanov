package ru.otus.hw.config;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoReactiveDriver getConnectionDriver(MongoClient mongoClient) {


        // For mongodb-sync-v4-driver
        MongoReactiveDriver driver = MongoReactiveDriver.withDefaultLock(mongoClient, databaseName);
        // For mongodb-v3-driver
        //MongoCore3Driver driver = MongoCore3Driver.withDefaultLock(mongoClient, databaseName);
        driver.setWriteConcern(WriteConcern.MAJORITY.withJournal(false).

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
