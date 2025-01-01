package ru.otus.hw.services;

import ru.otus.hw.models.in.Book;
import ru.otus.hw.models.out.BookNew;

public interface BookService {

    BookNew getBookNew(Book book);

    void reserveSequenceValues();
}
