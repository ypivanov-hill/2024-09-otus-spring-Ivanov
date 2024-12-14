package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    private final BookRepository bookRepository;

    @Override
    public void deleteByFullName(String fullName) {
        Query authtorQuery = Query.query(Criteria.where("fullName").is(fullName));
        mongoTemplate.remove(authtorQuery, Author.class);

        Query bookQuery = Query.query(Criteria.where("author.fullName").is(fullName));
        List<String> bookIds = mongoTemplate.find(bookQuery, Book.class).stream().map(Book::getId).toList();
        for (String bookId : bookIds) {
            bookRepository.deleteBookById(bookId);
        }
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
