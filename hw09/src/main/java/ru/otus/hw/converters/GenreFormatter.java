package ru.otus.hw.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;
import ru.otus.hw.dto.GenreDto;

import java.text.ParseException;
import java.util.Locale;

@Slf4j
public class GenreFormatter implements Formatter<GenreDto> {

    @Override
    public GenreDto parse(String id, Locale locale) throws ParseException {
        log.info(" parse {}", id);
        return new GenreDto(id, null);
    }

    @Override
    public String print(GenreDto genre, Locale locale) {
        log.info(" print {}", genre.getId());
        return genre.getId();
    }
}
