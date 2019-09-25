package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(final String token) {
        this(token, "Bearer");
    }
}
