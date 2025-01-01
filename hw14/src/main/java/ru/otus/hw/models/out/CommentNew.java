package ru.otus.hw.models.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentNew {

    private long id;

    private long bookId;

    private String text;

}
