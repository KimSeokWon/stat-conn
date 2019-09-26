package com.seokwon.kim.quiz.bank.exception;

import com.seokwon.kim.quiz.bank.stat.model.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { NotFoundDeviceException.class })
    public ResponseEntity<Object> handleNotFound( NotFoundDeviceException ex, WebRequest request ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return handleExceptionInternal( ex, new ErrorResponse(false, ex.getMessage()), headers, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { BadRequestException.class})
    public ResponseEntity<Object> handleBadRequest( BadRequestException ex, WebRequest request ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return handleExceptionInternal( ex, new ErrorResponse(false, ex.getMessage()), headers, HttpStatus.BAD_REQUEST, request);
    }
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return handleExceptionInternal( ex, new ErrorResponse(false, ex.getMessage()), headers, HttpStatus.BAD_REQUEST, request);
    }
}
