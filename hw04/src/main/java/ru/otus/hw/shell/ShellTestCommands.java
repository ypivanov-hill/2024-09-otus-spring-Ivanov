package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

@ShellComponent(value = "Testing Application Commands")
@RequiredArgsConstructor
public class ShellTestCommands {

    private final StudentService studentService;

    private final TestService testService;

    private final ResultService resultService;

    private final LocalizedIOService localizedMessagesService;

    private Student student;

    @ShellMethod(value = "Login command", key = {"l", "login"})
    public String login() {
        student = studentService.determineCurrentStudent();
        if (student.firstName().isEmpty() || student.lastName().isEmpty()) {
            student = null;
            return localizedMessagesService.getMessage("ShellCommand.not.logged.in");
        } else {
            return String.format(localizedMessagesService.getMessage("ShellCommand.welcome" ,student.getFullName()));
        }
    }

    @ShellMethod(value = "Start test command", key = {"t", "test", "start"})
    @ShellMethodAvailability(value = "isStartTestCommandAvailable")
    public String startTest() {
        var testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
        var fullName = student.getFullName();
        student = null;
        return localizedMessagesService.getMessage("ShellCommand.goodbye", fullName);
    }

    private Availability isStartTestCommandAvailable() {
        return (student != null)
                ? Availability.available()
                : Availability.unavailable(localizedMessagesService.getMessage("ShellCommand.logged.first"));
    }
}
