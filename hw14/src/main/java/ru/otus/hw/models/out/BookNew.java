package ru.otus.hw.models.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookNew {

    private long id;

    private String title;

    private long authorId;

}
