package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@RequiredArgsConstructor
public class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public void deleteAuthorById(String id) {
        Query authtorQuery = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(authtorQuery, Author.class);
        Query bookQuery = Query.query(Criteria.where("author.id").is(id));
        mongoTemplate.findAllAndRemove(bookQuery, Book.class)
                .flatMap(book -> {
                    Query queryComment = Query.query(Criteria.where("book.$id").is(book.getId()));
                    mongoTemplate.remove(queryComment, Comment.class);
                    return null;
                });

         /*       .publishOn()
        List<ObjectId> bookIds =
                .stream()
                .map(b -> new ObjectId(b.getId()))
                .toList();
        Query queryComment = Query.query(Criteria.where("book.$id").in(bookIds));
        mongoTemplate.remove(queryComment, Comment.class).getDeletedCount();*/
    }
}
