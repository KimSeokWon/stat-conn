package com.seokwon.kim.quiz.bank.stat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ErrorResponse implements Serializable {
    private final boolean success;
    private final String msg;
}
