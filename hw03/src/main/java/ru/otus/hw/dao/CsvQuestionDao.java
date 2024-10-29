package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {

        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        List<Question> questionList;
        try (Reader reader = getFileFromResource()) {
            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .build();
            List<QuestionDto> bean = csvToBean.parse();
            questionList = bean.stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());
            // Использовать QuestionReadException
            // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
        } catch (IOException e) {
            throw new QuestionReadException("file not found! " + fileNameProvider.getTestFileName(), e);
        }
        return questionList;
    }

    private Reader getFileFromResource() throws FileNotFoundException {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileNameProvider.getTestFileName());
        if (inputStream  == null) {
            throw new QuestionReadException("file not found! " + fileNameProvider.getTestFileName());
        }
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }
}
