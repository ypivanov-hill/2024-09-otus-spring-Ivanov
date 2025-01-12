package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Slf4j
@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public void deleteAuthorById(String id) {
        Query authtorQuery = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(authtorQuery, Author.class)
                .subscribe(author -> log.debug("author deleted count {}", author.getDeletedCount()));
        Query bookQuery = Query.query(Criteria.where("author.id").is(id));
        mongoTemplate.findAllAndRemove(bookQuery, Book.class)
                .subscribe(book -> {
                    Query queryComment = Query.query(Criteria.where("book._id").is(book.getId()));
                    mongoTemplate.remove(queryComment, Comment.class)
                            .subscribe(comment -> log.debug("comment deleted count {}", comment.getDeletedCount()));
                });
    }
}
