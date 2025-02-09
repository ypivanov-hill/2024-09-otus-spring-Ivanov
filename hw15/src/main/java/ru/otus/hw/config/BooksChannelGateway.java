package ru.otus.hw.config;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.models.in.BookNew;

@MessagingGateway
public interface BooksChannelGateway {
    @Gateway(requestChannel = "newBooksManualChannel")
    BookNew send(BookNew bookNew);
}
