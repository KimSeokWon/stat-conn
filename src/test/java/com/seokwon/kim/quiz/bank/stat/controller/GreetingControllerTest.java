package com.seokwon.kim.quiz.bank.stat.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GreetingControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void greetingTest() {
        this.webClient.get().uri("/greeting").exchange().expectStatus().isOk().expectBody(String.class)
                .isEqualTo("{\"id\":1,\"content\":\"Hello, World!\"}");
    }
}
