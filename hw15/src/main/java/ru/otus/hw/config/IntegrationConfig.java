package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ru.otus.hw.services.BookService;

import java.util.List;

@Slf4j
@Configuration
public class IntegrationConfig {

    @Value("${poller.pollingRate}")
    private int pollingRate;

    @Value("${poller.maxMessagesPerPoll}")
    private int maxMessagesPerPoll;

    @Bean(name = "newBooksChannel")
    public MessageChannelSpec<?, ?> newBooksChannel() {
        return MessageChannels.direct("newBooksChannel");
    }

    @Bean(name = "newBooksManualChannel")
    public MessageChannelSpec<?, ?> newBooksManualChannel() {
        return MessageChannels.direct("newBooksManualChannel");
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {
        return  Pollers.fixedRate(pollingRate).maxMessagesPerPoll(maxMessagesPerPoll).getObject();
    }

    @Bean
    public IntegrationFlow readFlow(BookService bookService,
                                    @Qualifier("newBooksChannel") MessageChannelSpec<?, ?> newBooksChannel) {
        return IntegrationFlow
                .from(newBooksChannel)
                .split()
                .<BookNew, Book>transform(bookService::getBook)
                .aggregate()
                .log()
                .handle(bookService::insertBookNew)
                .get();
    }

    @Bean
    public IntegrationFlow manualReadFlow(BookService bookService,
                                          @Qualifier("newBooksManualChannel")
                                              MessageChannelSpec<?, ?> newBooksManualChannel,
                                          @Qualifier("newBooksChannel")
                                              MessageChannelSpec<?, ?> newBooksChannel) {
        return IntegrationFlow
                .from(newBooksManualChannel)
                .<BookNew>filter(f -> !bookService.existsByTitle(f.getTitle()))
                .<BookNew, List<BookNew>>transform(List::of)
                .channel(newBooksChannel)
                .get();
    }

}
