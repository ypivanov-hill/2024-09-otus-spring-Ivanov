package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void deleteByFullName(String fullName) {

        Query authorQuery = Query.query(Criteria.where("fullName").is(fullName));
        Author author = mongoTemplate.findAndRemove(authorQuery,Author.class);

        Query bookQuery = Query.query(Criteria.where("author._id").is(new ObjectId(author.getId())));
        mongoTemplate.findAllAndRemove(bookQuery, Book.class);
    }
}
