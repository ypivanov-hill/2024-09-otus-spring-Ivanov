package ru.otus.hw.services;

import org.springframework.messaging.Message;
import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.in.BookNew;

import java.util.List;

public interface BookService {

    Book getBook(BookNew bookNew);

    void insertBookNew(Message<?> message);

    List<BookNew> addNewBooks(int count);
}
