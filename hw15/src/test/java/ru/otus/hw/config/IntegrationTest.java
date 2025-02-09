package ru.otus.hw.config;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.models.in.BookNew;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IntegrationTest {

    @MockBean
    private PollerMetadata defaultPoller;

    @Autowired
    private BooksChannelGateway booksChannelGateway;

    @Autowired
    private BookRepository bookRepository;

    @DisplayName("отправить сообщение и проверять результат работы")
    @Test
    void testIntegration() throws Exception {
        var actualBooks = bookRepository.findAll();
        int bookCount = actualBooks.size();
        System.out.println("bookCount: " + bookCount);

        BookNew bookNew = new BookNew("title", "Author_2", List.of("Genre_4", "Genre_5"));
        booksChannelGateway.send(bookNew);

        actualBooks = bookRepository.findAll();
        assertThat(actualBooks).hasSize(bookCount + 1).anyMatch(book -> book.getTitle().equals(bookNew.getTitle()));
        actualBooks.forEach(System.out::println);
    }
}
