package ru.otus.hw.formatter;

import lombok.AllArgsConstructor;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

import java.beans.PropertyEditorSupport;

@AllArgsConstructor
public class AuthorPropertyEditor extends PropertyEditorSupport {

    private final AuthorService authorService;

    @Override
    public void setAsText(String id) {
        final AuthorDto authorDto = authorService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invalid author Id:" + id));
        setValue(authorDto);
    }

    @Override
    public String getAsText() {
        return ((AuthorDto) getValue()).getId();
    }
}