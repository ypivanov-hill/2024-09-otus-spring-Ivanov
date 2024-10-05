package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao question;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        List<Question> questionList  = question.findAll();
        questionList.forEach(question -> {
            ioService.printFormattedLine(question.text() + "%n");
            question.answers()
                    .forEach(answer -> {
                        ioService.printFormattedLine("%x. " + answer.text(), question.answers().indexOf(answer));
                    });
            ioService.printLine("");
        });
    }
}
