package ru.otus.hw.models.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorNew {

    private long id;

    private String fullName;
}
