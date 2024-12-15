package ru.otus.hw.repositories;

import com.mongodb.BasicDBObject;
import ru.otus.hw.dto.BookCountByGenreDto;

import java.util.List;

public interface BookRepositoryCustom {

    List<BookCountByGenreDto> getBookCountByGenre();

    void deleteBookByTitle(String title);

    void deleteBookById(String id);
}
