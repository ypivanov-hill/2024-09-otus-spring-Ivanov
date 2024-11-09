package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    private long id;

    private String title;

    private Author author;
    @MappedCollection(idColumn = "info_main_id")
    private List<Genre> genres;
}
