package ru.otus.hw.services;

import ru.otus.hw.models.in.Author;
import ru.otus.hw.models.out.AuthorNew;

public interface AuthorService {

    AuthorNew getAuthorNew(Author author);

    void reserveSequenceValues();

}
