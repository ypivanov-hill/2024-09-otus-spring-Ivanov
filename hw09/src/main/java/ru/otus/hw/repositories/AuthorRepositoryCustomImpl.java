package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.util.List;

@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void deleteByFullName(String fullName) {
        Author author = findOneByFullName(fullName);
        mongoTemplate.remove(author);
        Query bookQuery = Query.query(Criteria.where("author._id").is(new ObjectId(author.getId())));
        mongoTemplate.remove(bookQuery, Book.class);
    }

    @Override
    public List<Author> findByFullName(String fullName) {
        Query authorQuery = Query.query(Criteria.where("fullName").is(fullName));
        return mongoTemplate.find(authorQuery,Author.class);
    }

    @Override
    public Author findOneByFullName(String fullName) {
        List<Author> authors = findByFullName(fullName);
        if (authors.isEmpty()) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(fullName));
        }
        if (authors.size() > 1) {
            throw new EntityNotFoundException("The author has a namesake.");
        }
        return authors.get(0);
    }
}
