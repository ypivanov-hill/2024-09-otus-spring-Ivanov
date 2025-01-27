package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.models.Book;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<BookCountByGenreDto> getBookCountByGenre() {

        Aggregation aggregation = newAggregation(
                unwind("genres")
                , group("genres.name").count().as("count")
                , sort(Sort.by(Sort.Direction.ASC, "_id"))
        );
        return mongoTemplate.aggregate(aggregation, Book.class, BookCountByGenreDto.class);

    }
}
