package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestServiceSimpleTest {

    private TestService testService;

    private IOService ioService;

    private QuestionDao question;

    @BeforeEach
    public void beforeEach(){
        System.out.println("Execute beforeEach " + this.hashCode());
        ioService = mock(IOService.class);
        question = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService,question);
        System.out.println("Execute Done " +this.hashCode());
    }

    @DisplayName("TestService Is Executable")
    @Test
    public void testServiceIsExecute() {
        System.out.println("Execute testServiceIsExecute " + this.hashCode());
        assertThat(testService).isNotNull();
        testService.executeTest();
        verify(question, times(1)).findAll();
    }

    @AfterEach
    public void afterEach(){
        System.out.println("Execute afterEach ");
    }
}
