package ru.otus.hw.repositories;

import com.mongodb.BasicDBObject;

import java.util.List;

public interface BookRepositoryCustom {

    List<BasicDBObject> getBookCountByGenre();

    void deleteBookByTitle(String title);

    void deleteBookById(String id);
}
