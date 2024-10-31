package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String INVITATION_CODE = "TestService.enter.answer.number";

    private static final String ERROR_CODE = "TestService.enter.answer.wrong";

    private static final String START_TEST_CODE = "TestService.answer.the.questions";

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        printTestHeader();
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            printQuestion(question);
            var isAnswerValid = readAnswerAndCheckIt(question.answers()); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printTestHeader() {
        ioService.printLine("");
        ioService.printLineLocalized(START_TEST_CODE);
        ioService.printLine("");
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

    private boolean readAnswerAndCheckIt(List<Answer> answers) {
        int maxAnswerNum = answers.size() - 1;
        int answerNumber = ioService.readIntForRangeWithPromptLocalized(0, maxAnswerNum, INVITATION_CODE, ERROR_CODE);
        return answers.get(answerNumber).isCorrect();
    }

}
