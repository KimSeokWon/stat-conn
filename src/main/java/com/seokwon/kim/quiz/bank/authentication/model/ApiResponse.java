package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}
