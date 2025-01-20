package ru.otus.hw.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCountByGenreDto;

public interface BookRepositoryCustom {

    Mono<String> deleteBookById(String id);

    Flux<BookCountByGenreDto> getBookCountByGenre();
}
