package com.seokwon.kim.quiz.bank.stat.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.seokwon.kim.quiz.bank.statdevice"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeviceControllerTest  {
    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void successToGetDevices() throws Exception {
        this.webTestClient.get().uri("/api/code-device/devices").exchange().expectStatus().isOk();;

    }
}
