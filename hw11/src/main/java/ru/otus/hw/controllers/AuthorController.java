package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

//RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    //@GetMapping("/api/v1/author")
    public Flux<AuthorDto> findAllAuthor() {
        return authorService.findAll();
    }

    //@DeleteMapping("/api/v1/author/{id}")
    public Mono<String> deleteById(@PathVariable String id) {
        return authorService.deleteById(id);
    }

    //@GetMapping("/api/v1/author/{id}")
    public Mono<AuthorDto> findById(@PathVariable String id) {
        return authorService.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Entity Not Found")));
    }
}
