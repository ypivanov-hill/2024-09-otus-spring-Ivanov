package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void deleteById(String id) {
        Query authtorQuery = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(authtorQuery, Author.class);
        Query bookQuery = Query.query(Criteria.where("author.id").is(id));
        List<ObjectId> bookIds = mongoTemplate.findAllAndRemove(bookQuery, Book.class)
                .stream()
                .map(b -> new ObjectId(b.getId()))
                .toList();
        Query queryComment = Query.query(Criteria.where("book.$id").in(bookIds));
        mongoTemplate.remove(queryComment, Comment.class).getDeletedCount();
    }
}
