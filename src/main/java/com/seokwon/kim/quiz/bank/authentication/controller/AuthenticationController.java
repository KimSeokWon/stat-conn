package com.seokwon.kim.quiz.bank.authentication.controller;

import com.seokwon.kim.quiz.bank.authentication.model.*;
import com.seokwon.kim.quiz.bank.authentication.service.AuthenticationService;
import com.seokwon.kim.quiz.bank.stat.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest user) {
        return ResponseEntity.ok(new JwtAuthenticationResponse(authenticationService.authenticate(user)));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        User user = authenticationService.registerUser(signUpRequest);

        return ResponseEntity.ok().body(new ErrorResponse(true, "Register user is success."));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok(new JwtAuthenticationResponse(authenticationService.refresh()));
    }
}
