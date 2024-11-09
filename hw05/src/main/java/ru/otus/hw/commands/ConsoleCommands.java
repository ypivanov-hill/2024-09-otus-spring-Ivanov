package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import org.h2.tools.Console;

import java.sql.SQLException;


@RequiredArgsConstructor
@ShellComponent
public class ConsoleCommands {

    @ShellMethod(value = "Run console", key = "c")
    public String runConsole()  {
        try {
            String[] args = new String[0];
            Console.main(args);
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        return "Ok";
    }
}