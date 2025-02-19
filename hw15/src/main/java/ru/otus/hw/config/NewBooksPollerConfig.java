package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;
import ru.otus.hw.services.BookService;

@Configuration
@RequiredArgsConstructor
public class NewBooksPollerConfig  {

    @Bean
    @InboundChannelAdapter(channel = "newBooksChannel")
    public MessageSource<?> newBooksChannelAdapter(BookService bookService) {
        return () -> new GenericMessage<>(bookService.addNewBooks(3));
    }
}
