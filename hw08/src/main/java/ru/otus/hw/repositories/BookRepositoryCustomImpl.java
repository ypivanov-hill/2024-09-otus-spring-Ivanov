package ru.otus.hw.repositories;

import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Map<String, Long> getBookCountByGenre() {
        Map<String, Long> resultMap = new HashMap<>();

        Aggregation aggregation = newAggregation(
                unwind("genres")
                , group("genres.name").count().as("count")
                , sort(Sort.by(Sort.Direction.ASC, "_id"))
        );
        mongoTemplate.aggregate(aggregation,Book.class, BasicDBObject.class)
                .getMappedResults()
                .forEach(row -> resultMap.put(row.getString("_id"), row.getLong("count")));

        return resultMap;

    }

    @Override
    public void deleteBookByTitle(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Book book = mongoTemplate.findOne(query, Book.class);
        deleteBookById(book.getId());
    }

    @Override
    public void deleteBookById(String id) {
        Query queryComment = Query.query(Criteria.where("book._id").is(id));
        mongoTemplate.remove(queryComment, Comment.class);
        Query queryBook = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(queryBook, Book.class);
    }
}
