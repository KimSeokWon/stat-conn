package com.seokwon.kim.quiz.bank.stat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seokwon.kim.quiz.bank.authentication.model.SignInRequest;
import com.seokwon.kim.quiz.bank.authentication.model.SignUpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
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
public class DeviceControllerTest  {
    @Autowired
    private WebTestClient webTestClient;

    private String token;

    @Before
    public void signIn() {
        final EntityExchangeResult<byte[]> bodyContent = this.webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignInRequest("test123111", "test123111")), SignInRequest.class)
                .exchange().expectBody().returnResult();
        if ( bodyContent.getStatus() == HttpStatus.UNAUTHORIZED ) {
            successToSignup();
            signIn();
            return;
        }
        ObjectMapper om = new ObjectMapper();
        try {
            token = "Bearer " + om.readTree(bodyContent.getResponseBodyContent()).get("token").asText();
        } catch ( IOException ex ) {

        }

    }
    public void successToSignup() {
        this.webTestClient.post().uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(new SignUpRequest("test123111", "test123111")), SignUpRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void successToGetDevices() throws Exception {
        this.webTestClient.get().uri("/api/code-device/devices").exchange().expectStatus().isOk();;

    }
}
