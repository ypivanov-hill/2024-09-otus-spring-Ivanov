package ru.otus.hw.exceptions;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handeNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found(");
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<String> handeRequestNotPermitted(RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request Not Permitted(");
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<String> handeRequestNotPermitted(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Call Not Permitted (");
    }

}
