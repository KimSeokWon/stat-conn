package com.seokwon.kim.quiz.bank.exception;

/**
 * 요청이 잘못 되었을때 발생하는 예외
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
    public BadRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
