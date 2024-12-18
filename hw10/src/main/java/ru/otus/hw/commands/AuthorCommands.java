package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // aaid
    @ShellMethod(value = "Find author by id", key = "aaid")
    public String findAuthorById(String id) {
        return authorService.findById(id).stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // aafn Author_1
    @ShellMethod(value = "Find author by Full Name", key = "aafn")
    public String findByFullName(String fullName) {
        return authorService.findByFullName(fullName).stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    //aad Author_1
    @ShellMethod(value = "Delete author by Full Name", key = "aad")
    public void deleteByFullName(String fullName) {
         authorService.deleteByFullName(fullName);
    }
}
