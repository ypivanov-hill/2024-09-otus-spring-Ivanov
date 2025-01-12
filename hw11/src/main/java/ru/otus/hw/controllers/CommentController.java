package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/v1/book/{bookId}/comment")
    public Flux<CommentDto> findAllComments(@PathVariable String bookId) {
         return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/{bookId}/comment")
    public Mono<CommentDto> editComment(@PathVariable String bookId,
                                        @RequestBody CommentDto commentDto) {
         if (commentDto == null || commentDto.getText().isEmpty()) {
            throw  new EntityNotFoundException("Input Entity is Empty");
        }
        return commentService.insert(commentDto.getText(), commentDto.getBook());
    }

    @PutMapping("/api/v1/book/{bookId}/comment")
    public Mono<CommentDto> updateComment(@PathVariable(required = true) String bookId,
                              @RequestBody CommentDto commentDto) {
        return commentService.update(commentDto.getId(), commentDto.getText(), commentDto.getBook());
    }

    @DeleteMapping("/api/v1/book/{bookId}/comment/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String bookId, @PathVariable String id) {
        commentService.deleteById(id);
        return ResponseEntity.ok(id);
    }

}
