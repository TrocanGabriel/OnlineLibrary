package com.online.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked","rawtypes"})
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest webRequest) {
        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        details.add(Arrays.toString(exception.getStackTrace()));
        ErrorResponse error = new ErrorResponse("Server Error", details);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleAccountEnabledException(RecordNotFoundException exception, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(exception.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Record not found!", details);
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

}