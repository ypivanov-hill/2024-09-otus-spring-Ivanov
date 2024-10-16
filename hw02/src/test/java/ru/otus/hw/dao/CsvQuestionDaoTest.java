package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CsvQuestionDaoTest {

    private static final String FILE_NAME = "questions-test.csv";

    private TestFileNameProvider fileNameProvider;

    private QuestionDao questionDao;

    @BeforeEach
    public void beforeEach(){
        fileNameProvider = mock(TestFileNameProvider.class);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @DisplayName("Test csv file reading")
    @Test
    public void testCsvFileReading() {
        given(fileNameProvider.getTestFileName()).willReturn(FILE_NAME);
        assertThat(fileNameProvider).isNotNull();
        assertThat(fileNameProvider.getTestFileName()).isEqualTo(FILE_NAME);
        assertThat(questionDao).isNotNull();
        List<Question> questionList = questionDao.findAll();
        assertThat(questionList).hasSize(2);
        assertThat(questionList).first().isInstanceOf(Question.class);
        assertThat(questionList)
                .filteredOn(question -> question.text().contains("QUESTION1"))
                .hasSize(1);
        assertThat(questionList)
                .anyMatch(question -> question.answers().size() == 3)
                .anyMatch(question -> question.text().contains("QUESTION2"))
                .anyMatch(question -> !question.text().contains("withSkipLines"));

    }
}
