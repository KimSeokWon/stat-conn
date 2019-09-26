package com.seokwon.kim.quiz.bank.stat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seokwon.kim.quiz.bank.authentication.model.SignInRequest;
import com.seokwon.kim.quiz.bank.authentication.model.SignUpRequest;

import org.junit.Before;
import org.junit.Test;
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
import java.time.Duration;
import java.time.Instant;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.seokwon.kim.quiz.bank"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionControllerTest {

    private Logger logger = LoggerFactory.getLogger(ConnectionControllerTest.class);
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

//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(connectionController).build();
//    }
    @Test
    public void successToGetMaxRateDeviceAnnually() throws Exception {

        this.webTestClient.get().uri("/api/device-stat/by-year")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange().expectStatus().isOk();
    }

    @Test
    public void successToGetMaxRateDeviceByYear() throws Exception {
        this.webTestClient.get().uri("/api/device-stat/year/2018")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange().expectStatus().isOk();
    }

    @Test
    public void successToGetMaxRateYearByDevice() throws Exception {
        Instant start = Instant.now();
        this.webTestClient.get().uri("/api/device-stat/device/DEVICE_00000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange().expectStatus().isOk();
        logger.debug("Elapsed Time in JUnit: {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    @Test
    public void successToPredictByDevice() {
        this.webTestClient.get().uri("/api/device-stat/predict/device/DEVICE_00000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange().expectStatus().isOk();
    }
}
