package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ShellComponent(value = "Testing Application Commands")
@RequiredArgsConstructor
public class ShellTestCommands {

    private final StudentService studentService;
    private final TestService testService;
    private final ResultService resultService;
    private final LocalizedIOService localizedMessagesService;

    private Student student;

    private List<Object> availableCharsets = Lists.newArrayList((Charset.availableCharsets().keySet().stream().toArray()));

    @ShellMethod(value = "Login command", key = {"l", "login"})
    public String login(){
        student = studentService.determineCurrentStudent();
        if(student.firstName().isEmpty() || student.lastName().isEmpty()){
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
        System.out.println("Def " + Charset.defaultCharset().displayName());

       // for (var s : Charset.availableCharsets().keySet()) {
          //  System.out.println("Def " + s);
        /*    for (var t : Charset.availableCharsets().keySet()) {
                String result = new String(localizedMessagesService.getMessage("ShellCommand.logged.first").getBytes(StandardCharsets.UTF_8), Charset.forName(t));
                System.out.println( "From " /*+ s + " to " + t + " result " + result);
            }*/
       // }


       String availableCharset = availableCharsets.get(0).toString();
        System.out.println("Def " +availableCharset);
        availableCharsets.remove(0);
        return (student != null)
                ? Availability.available()
                : Availability.unavailable(new String (localizedMessagesService.getMessage("ShellCommand.logged.first").getBytes(Charset.defaultCharset()), Charset.forName(availableCharset)));
    }
}
