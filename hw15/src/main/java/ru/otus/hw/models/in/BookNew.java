package ru.otus.hw.models.in;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookNew {

    private String title;

    private String authorName;

    private List<String> genreNames;
}
