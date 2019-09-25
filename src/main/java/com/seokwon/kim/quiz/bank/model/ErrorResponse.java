package com.seokwon.kim.quiz.bank.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ErrorResponse implements Serializable {
    private final int code;
    private final String msg;
}
