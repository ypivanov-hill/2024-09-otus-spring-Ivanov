package ru.otus.hw.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.otus.hw.dto.GenreDto;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Converter
public class StringListConverter implements AttributeConverter<List<GenreDto>, String[]> {
    private static final String SPLIT_CHAR = ",";

    // Go nuts on List to string here...

    @Override
    public String[] convertToDatabaseColumn(List<GenreDto> stringList) {
        return stringList.stream().map(Objects::toString).toArray(String[]::new);//);// String.join(SPLIT_CHAR, stringList.toString());
    }

    @Override
    public List<GenreDto> convertToEntityAttribute(String string[]) {
        return List.of();// List<GenreDto> = // rrays.asList(string.split(SPLIT_CHAR));
    }
}