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

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    private final CommentRepository commentRepository;

    @Override
    public List<BasicDBObject> getBookCountByGenre() {
        Aggregation aggregation = newAggregation(

                 unwind("genres")
                , group("genres.name").count().as("count")
                , sort(Sort.by(Sort.Direction.ASC, "_id"))
        );

        return mongoTemplate.aggregate(aggregation, Book.class, BasicDBObject.class).getMappedResults();

    }

    @Override
    public void deleteBookByTitle(String title) {
        Query query = Query.query(Criteria.where("title").is(title));
        Book book = mongoTemplate.findOne(query,Book.class);
        deleteBookById(book.getId());
    }

    @Override
    public void deleteBookById(String id) {
       List<Comment> comments = commentRepository.findByBookId(id);
       comments.forEach(comment -> commentRepository.deleteById(comment.getId()));
       Query query = Query.query(Criteria.where("id").is(id));
       mongoTemplate.findAndRemove(query,Book.class);
    }
}
