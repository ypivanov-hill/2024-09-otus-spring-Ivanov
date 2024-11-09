package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    private static final String FILE_NAME = "questions-test.csv";

    @MockBean
    private TestFileNameProvider fileNameProvider;
    @Autowired
    private QuestionDao questionDao;

    @DisplayName("Test csv file reading")
    @Test
    public void testCsvFileReading() {
        given(fileNameProvider.getTestFileName()).willReturn(FILE_NAME);
        assertThat(fileNameProvider).isNotNull();
        assertThat(fileNameProvider.getTestFileName()).isEqualTo(FILE_NAME);
        assertThat(questionDao).isNotNull();
        List<Question> questionList = questionDao.findAll();
        assertThat(questionList).hasSize(2);
        assertThat(questionList)
                .filteredOn(question -> question.text().contains("QUESTION1"))
                .hasSize(1);
        assertThat(questionList)
                .anyMatch(question -> question.answers().size() == 3)
                .anyMatch(question -> question.text().contains("QUESTION2"))
                .anyMatch(question -> !question.text().contains("withSkipLines"));

    }
}
