package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao question;

    @Override
    public void executeTest() {
        printTestHeader();
        // Получить вопросы из дао и вывести их с вариантами ответов
        List<Question> questionList = question.findAll();
        questionList.forEach(question -> {
            printQuestion(question);
        });
     }

    private void printTestHeader() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
    }

    private void printQuestion(Question question) {
        ioService.printFormattedLine(question.text() + "%n");
        printAnswers(question.answers());
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%x. " + answers.get(i).text(), i);
        }
        ioService.printLine("");
    }
}
