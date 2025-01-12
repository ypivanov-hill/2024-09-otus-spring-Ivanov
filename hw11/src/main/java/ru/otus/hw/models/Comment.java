package ru.otus.hw.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Comment {

    @Id
    private String id;

    private String text;

    private Book book;

    public Comment(String text, Book book) {
        this.text = text;
        this.book = book;
    }
}
