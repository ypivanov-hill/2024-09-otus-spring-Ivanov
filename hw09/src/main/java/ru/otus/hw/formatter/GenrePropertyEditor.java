package ru.otus.hw.formatter;

import lombok.AllArgsConstructor;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

import java.beans.PropertyEditorSupport;

@AllArgsConstructor
public class GenrePropertyEditor extends PropertyEditorSupport {

    private final GenreService genreService;

    @Override
    public void setAsText(String id) {
        final GenreDto genreDto = genreService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid genre Id:" + id));
        setValue(genreDto);
    }

    @Override
    public String getAsText() {
        return ((GenreDto) getValue()).getId();
    }
}