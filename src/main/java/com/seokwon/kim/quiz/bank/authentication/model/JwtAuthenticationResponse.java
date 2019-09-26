package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtAuthenticationResponse {
    private String token;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(final String token) {
        this.token = token;
    }
}
