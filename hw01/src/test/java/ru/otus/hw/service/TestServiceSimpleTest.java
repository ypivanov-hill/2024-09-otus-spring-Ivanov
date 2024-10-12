package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestServiceSimpleTest {

    private TestService testService;

    private IOService ioService;

    private QuestionDao questionDao;

    @BeforeEach
    public void beforeEach(){
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService,questionDao);
    }

    @DisplayName("TestService Is Executable")
    @Test
    public void testServiceIsExecute() {
        assertThat(testService).isNotNull();
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
    }
}
