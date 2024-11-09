package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.dao.QuestionDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class TestServiceSimpleTest {

    @MockBean
    private LocalizedIOService localizedIOService;
    @MockBean
    private QuestionDao questionDao;
    @MockBean
    private StudentService studentService;

    @Autowired
    private TestService testService;

    @DisplayName("TestService Is Executable")
    @Test
    public void testServiceIsExecute() {
        assertThat(testService).isNotNull();
        testService.executeTestFor(studentService.determineCurrentStudent());
        verify(questionDao, times(1)).findAll();
        verify(studentService, times(1)).determineCurrentStudent();
    }
}
