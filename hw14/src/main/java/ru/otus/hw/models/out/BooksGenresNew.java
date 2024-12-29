package ru.otus.hw.models.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BooksGenresNew {

    private long bookId;

    private long genreId;
}
