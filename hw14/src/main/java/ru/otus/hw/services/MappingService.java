package ru.otus.hw.services;

import ru.otus.hw.models.out.BooksGenresNew;

import java.util.List;

public interface MappingService {

    long getAuthorNewIdByOldId(String oldId);

    long getBookNewIdByOldId(String oldId);

    long getGenreNewIdByOldId(String oldId);

    List<BooksGenresNew> getBookToGenreList();

    void putAuthorIds(long newId, String oldId);

    void putBookIds(long newId, String oldId);

    void putGenreIds(long newId, String oldId);

    void putBookToGenreIds(long bookNewId, long genreNewId);

    void cleanUp();

    void info();

}
