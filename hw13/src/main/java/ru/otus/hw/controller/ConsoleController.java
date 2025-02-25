package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Console;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConsoleController {
    @GetMapping("/console")
    public String getConsole() {

        try {
            String[] args = new String[0];
            Console.main(args);
        } catch (SQLException e) {
            log.info("Invalid console: {}", e.getMessage());
        }
        return "redirect:/";
    }
}
