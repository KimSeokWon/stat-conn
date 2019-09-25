package com.seokwon.kim.quiz.bank.exception;

public class InvalidYearException extends RestAbstractException {
    public InvalidYearException() {
        super("Year is invalidate");
    }
}
