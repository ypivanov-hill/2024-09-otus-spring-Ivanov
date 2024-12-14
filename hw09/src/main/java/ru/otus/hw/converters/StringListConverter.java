package ru.otus.hw.converters;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;


@Component
@Slf4j
public class StringListConverter implements Converter<String, GenreDto> {
    private static final String SPLIT_CHAR = ",";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public GenreDto convert(String source) {
        log.info("source {}", source);

        JsonNode jsonNode = null;
        source = source.replace("GenreDto(", "{").replace(")","}");
        try {
            jsonNode = mapper.readTree(source);

            return new GenreDto(jsonNode.get("id").asText(), jsonNode.get("name").asText());
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException {}", e.getMessage());
        }
        return null;//List.of();
    }

    // Go nuts on List to string here...

   /* @Override
    public String convertToDatabaseColumn(GenreDto stringList) {
        log.info("StringListConverter convertToDatabaseColumn !!!!!!!!!!!!!");
        return "sdf s"; //stringList.stream().map(Objects::toString).toArray(String[]::new);//);// String.join(SPLIT_CHAR, stringList.toString());
    }

    @Override
    public GenreDto convertToEntityAttribute(String string) {
        log.info("StringListConverter convertToEntityAttribute !!!!!!!!!!!!!");
        return null;//List.of();// List<GenreDto> = // rrays.asList(string.split(SPLIT_CHAR));
    }*/
}