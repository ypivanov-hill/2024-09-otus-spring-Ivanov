package ru.otus.hw.services;

import ru.otus.hw.models.in.Genre;
import ru.otus.hw.models.out.GenreNew;


public interface GenreService {

    GenreNew getGenreNew(Genre genre);
}
