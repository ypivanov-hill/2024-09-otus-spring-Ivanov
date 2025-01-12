package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public Flux<AuthorDto> findAllAuthor() {
        return authorService.findAll();
    }

    @DeleteMapping("/api/v1/author/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        authorService.deleteById(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/api/v1/author/{id}")
    public Mono<AuthorDto> findById(@PathVariable String id) {
        return authorService.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Entity Not Found")));
    }
}
