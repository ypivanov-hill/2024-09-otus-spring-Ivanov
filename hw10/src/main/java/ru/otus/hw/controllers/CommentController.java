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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping("/api/v1/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> findAllComments(@PathVariable String bookId) {
        bookService.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid book Id:" + bookId));
        return ResponseEntity.ok(findAllCommentsLocal(bookId));
    }

    @PostMapping("/api/v1/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> editComment(@PathVariable String bookId,
                                                        @RequestBody CommentDto commentDto) {

        if (commentDto != null && !commentDto.getText().isEmpty()) {
            commentService.insert(commentDto.getText(), commentDto.getBook());
        }

        return ResponseEntity.ok(findAllCommentsLocal(bookId));
    }

    @PutMapping("/api/v1/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> updateComment(@PathVariable(required = true) String bookId,
                              @RequestBody CommentDto commentDto) {
        commentService.update(commentDto.getId(), commentDto.getText(), commentDto.getBook());

        return ResponseEntity.ok(findAllCommentsLocal(bookId));
    }

    @DeleteMapping("/api/v1/book/{bookId}/comment/{id}")
    public ResponseEntity<List<CommentDto>> deleteById(@PathVariable String bookId, @PathVariable String id) {
        commentService.deleteById(id);
        return ResponseEntity.ok(findAllCommentsLocal(bookId));
    }

    private List<CommentDto> findAllCommentsLocal(String bookId) {

        return commentService.findByBookId(bookId);
    }
}
