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

    private static final String INVITATION_TEXT = "Enter answer number:";

    private static final String ERROR_TEXT = "Wrong number. Try again:";

    private final IOService ioService;

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

    private Boolean readAnswerAndCheckIt(List<Answer> answers) {
        int maxAnswerNum = answers.size() - 1;
        int answerNumber = ioService.readIntForRangeWithPrompt(0, maxAnswerNum, INVITATION_TEXT, ERROR_TEXT);
        return answers.get(answerNumber).isCorrect();
    }

}
