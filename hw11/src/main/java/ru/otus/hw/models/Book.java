package ru.otus.hw.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Book {

    @Id
    private String id;

    private String title;

    private Mono<Author> author;

    private Flux<Genre> genres;


    public Book(String title, Mono<Author> author, Flux<Genre> genres) {
        this.title = title;
        this.author = author;
        this.genres = genres;
    }
}
