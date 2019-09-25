package com.seokwon.kim.quiz.bank.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { NotFoundDeviceException.class })
    public ResponseEntity<Object> handleNotFound( NotFoundDeviceException ex, WebRequest request ) {
        return handleExceptionInternal( ex, ex.getBody(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
