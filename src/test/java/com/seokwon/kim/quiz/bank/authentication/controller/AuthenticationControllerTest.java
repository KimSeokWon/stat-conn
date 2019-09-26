package com.seokwon.kim.quiz.bank.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seokwon.kim.quiz.bank.authentication.model.SignInRequest;
import com.seokwon.kim.quiz.bank.authentication.model.SignUpRequest;
import com.seokwon.kim.quiz.bank.authentication.repository.RoleRepository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.seokwon.kim.quiz.bank"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    private Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    @Autowired
    private RoleRepository roleRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void successToSignin() {
        successToSignup();
        this.webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignInRequest("test123111", "test123111")), SignInRequest.class)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void failToSignupForInvalidInputData() {
        this.webTestClient.post().uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignUpRequest("test123", "test123")), SignUpRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void successToSignup() {
        this.webTestClient.post().uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignUpRequest("test123111", "test123111")), SignUpRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    public String getToken() {
        final EntityExchangeResult<byte[]> bodyContent = this.webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignInRequest("test123111", "test123111")), SignInRequest.class)
                .exchange().expectBody().returnResult();
        if ( bodyContent.getStatus() == HttpStatus.UNAUTHORIZED ) {
            successToSignup();
            return getToken();
        }
        ObjectMapper om = new ObjectMapper();
        try {
            return "Bearer " + om.readTree(bodyContent.getResponseBodyContent()).get("token").asText();
        } catch ( IOException ex ) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("Error at getToken()");
        }

    }

    @Test(timeout = 3000000)
    public void successToRefresh() {
        final String token = getToken();
        Assert.assertNotNull(token);
        this.webTestClient.get().uri("/auth/refresh")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange()
                .expectStatus().isOk();
    }
}
