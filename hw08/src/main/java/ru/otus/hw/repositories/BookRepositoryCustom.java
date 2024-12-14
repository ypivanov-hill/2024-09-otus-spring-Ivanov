package ru.otus.hw.repositories;

import java.util.Map;

public interface BookRepositoryCustom {

    Map<String, Long> getBookCountByGenre();

    void deleteBookByTitle(String title);

    void deleteBookById(String id);
}
