package ru.otus.hw.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.config.BooksChannelGateway;
import ru.otus.hw.models.in.BookNew;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Slf4j
@ShellComponent
public class ConsoleCommands {

    private final ApplicationContext ctx;

    private final BooksChannelGateway booksChannelGateway;

    // a BookTitle_NEW Author_2 Genre_4,Genre_5
    @ShellMethod(value = "addNewBook", key = "a")
    public void addNewBook(String title, String  authorIName, Set<String> genresNames) throws Exception {
        BookNew bookNew = new BookNew(title, authorIName, genresNames.stream().toList());
        booksChannelGateway.send(bookNew);
    }

    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        Map<String, MessageChannel> channels = ctx.getBeansOfType(MessageChannel.class);
        log.info("CHANNELS:");
        int i = 0;
        for (Map.Entry<String, MessageChannel> entry : channels.entrySet()) {
            log.info("{}. {}/{} -> {}",
                    ++i,
                    entry.getKey(),
                    entry.getValue().getClass().getSimpleName(),
                    entry.getValue());
        }
        log.info("HANDLERS:");
        i = 0;
        Map<String, MessageHandler> endpoints = ctx.getBeansOfType(MessageHandler.class);
        for (Map.Entry<String, MessageHandler> entry : endpoints.entrySet()) {
            log.info("{}. {}/{} -> {}",
                    ++i,
                    entry.getKey(),
                    entry.getValue().getClass().getSimpleName(),
                    entry.getValue());
        }


    }
}
