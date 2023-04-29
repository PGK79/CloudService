package ru.netology.cloudservice.advice;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.exception.InputDataException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.Error;

import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice
public class AllExceptionHandlers {
    private AtomicInteger counter = new AtomicInteger(1);
    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity<Error> authorizeExceptionHandler(AuthorizeException e) {
        return new ResponseEntity<>(new Error(e.getMessage(),counter.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Error> tokenExceptionHandler(TokenException e) {
        return new ResponseEntity<>(new Error(e.getMessage(),counter.getAndIncrement()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<Error> inputDataExceptionHandler(InputDataException e) {
        return new ResponseEntity<>(new Error(e.getMessage(),counter.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<Error> FileSizeLimitExceededExceptionHandler(FileSizeLimitExceededException e) {
        return new ResponseEntity<>(new Error(e.getMessage(),counter.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> ConstraintViolationExceptionHandler(ConstraintViolationException e) {
        return new ResponseEntity<>(new Error("Файл с таким именем уже сохранен",counter.getAndIncrement()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Error> RepositoryExceptionHandler(RepositoryException e) {
        return new ResponseEntity<>(new Error(e.getMessage(),counter.getAndIncrement()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}