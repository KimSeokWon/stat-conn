package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class SignInRequest {
    private String username;
    private String password;
}
