package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.BookCountByGenreDto;

public interface BookRepositoryCustom {

    void deleteBookById(String id);

    Flux<BookCountByGenreDto> getBookCountByGenre();
}
