package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw.service.TestRunnerService;

@PropertySource("classpath:application.yml")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        ApplicationContext context = SpringApplication.run(Application.class, args);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}