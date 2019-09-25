package com.seokwon.kim.quiz.bank.stat.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.Instant;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.seokwon.kim.quiz.bank.statdevice"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionControllerTest {

    Logger logger = LoggerFactory.getLogger(ConnectionControllerTest.class);
    @Autowired
    private WebTestClient webTestClient;

//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(connectionController).build();
//    }
    @Test
    public void successToGetMaxRateDeviceAnnually() throws Exception {

        this.webTestClient.get().uri("/api/device-stat/by-year").exchange().expectStatus().isOk();
    }

    @Test
    public void successToGetMaxRateDeviceByYear() throws Exception {
        this.webTestClient.get().uri("/api/device-stat/year/2018").exchange().expectStatus().isOk();
    }

    @Test
    public void successToGetMaxRateYearByDevice() throws Exception {
        Instant start = Instant.now();
        this.webTestClient.get().uri("/api/device-stat/device/DEVICE_00000").exchange().expectStatus().isOk();
        logger.debug("Elapsed Time in JUnit: {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    @Test
    public void successToPredictByDevice() {
        this.webTestClient.get().uri("/api/predict/device/DEVICE_00000").exchange().expectStatus().isOk();
    }



}