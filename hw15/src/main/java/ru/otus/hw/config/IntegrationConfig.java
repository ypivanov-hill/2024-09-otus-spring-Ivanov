package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.BookNew;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.services.BookService;

import java.util.List;

@Slf4j
@Configuration
public class IntegrationConfig {

    @Value("${poller.pollingRate}")
    private int pollingRate;

    @Value("${poller.maxMessagesPerPoll}")
    private int maxMessagesPerPoll;

    @Bean
    public MessageChannelSpec<?, ?> newBooksChannel() {
        return MessageChannels.direct("newBooksChannel");
    }

    @Bean
    public MessageChannelSpec<?, ?> newBooksManualChannel() {
        return MessageChannels.direct("newBooksManualChannel");
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {
        return  Pollers.fixedRate(pollingRate).maxMessagesPerPoll(maxMessagesPerPoll).getObject();
    }

    @Bean
    public IntegrationFlow readFlow(BookService bookService) {
        return IntegrationFlow
                .from("newBooksChannel")
                .split()
                .<BookNew, Book>transform(bookService::getBook)
                .aggregate()
                .log()
                .handle(bookService::insertBookNew)
                .get();
    }

    @Bean
    public IntegrationFlow manualReadFlow(BookRepository bookRepository) {
        return IntegrationFlow
                .from("newBooksManualChannel")
                .<BookNew>filter(f -> bookRepository.findByTitle(f.getTitle()).isEmpty())
                .<BookNew, List<BookNew>>transform(List::of)
                .channel("newBooksChannel")
                .get();
    }

}
