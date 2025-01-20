package ru.otus.hw.repositories;

import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCountByGenreDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

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

    @Override
    public Mono<String> deleteBookById(String id) {
        Query queryComment = Query.query(Criteria.where("book._id").is(id));
        Mono<DeleteResult> removeMono = mongoTemplate.remove(queryComment, Comment.class);
        Query queryBook = Query.query(Criteria.where("id").is(id));
        Mono<DeleteResult> removeCommnetMono = mongoTemplate.remove(queryBook, Book.class);
        return Mono.zip(removeMono, removeCommnetMono , (b, c) ->  id);
    }
}
