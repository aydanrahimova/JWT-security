package com.example.securityjwt.exception.handler;

import com.example.securityjwt.exception.AlreadyExistException;
import com.example.securityjwt.exception.ForbiddenException;
import com.example.securityjwt.exception.ResourceNotFoundException;
import com.example.securityjwt.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                errors.put("error", "Invalid input");
            }
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ExceptionDto handleUnauthorizedException(UnauthorizedException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ExceptionDto handlerForbiddenException(ForbiddenException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionDto handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return new ExceptionDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyExistException.class)
    public ExceptionDto handleAlreadyExistException(AlreadyExistException ex) {
        log.error(ex.getMessage());
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ExceptionDto handleGenericException(RuntimeException ex) {
        return new ExceptionDto(ex.getMessage());
    }


}
