package ru.otus.hw.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handeNotFoundException(EntityNotFoundException ex) {
        String errorText = ex.getMessage();
        return new ModelAndView("customError", "errorText", errorText);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handeException(HttpRequestMethodNotSupportedException ex) {
        String errorText = ex.getMessage();
        return new ModelAndView("customError", "errorText", errorText);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handeException(AccessDeniedException ex) {
        String errorText = ex.getMessage();
        return new ModelAndView("customError", "errorText", errorText);
    }

}
