package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotBlank
    @Size(min=5, max=256)
    private String username;
    @NotBlank
    @Size(min=9, max=20)
    private String password;
}
