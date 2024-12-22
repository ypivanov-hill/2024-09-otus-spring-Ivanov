package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Data
public class BookDto {

    private final String id;

    private final String title;

    private final AuthorDto author;

    private final List<GenreDto> genres;

    private final List<BookCommentDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return Objects.equals(id, bookDto.id) && Objects.equals(title, bookDto.title) && Objects.equals(author, bookDto.author) && Objects.equals(genres, bookDto.genres) && Objects.equals(comments, bookDto.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, genres, comments);
    }
}