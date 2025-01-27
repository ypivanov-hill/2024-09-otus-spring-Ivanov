package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.BookCountByGenreDto;

public interface BookRepositoryCustom {

    Flux<BookCountByGenreDto> getBookCountByGenre();
}
