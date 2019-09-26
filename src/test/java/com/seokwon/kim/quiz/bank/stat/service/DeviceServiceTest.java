package com.seokwon.kim.quiz.bank.stat.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceServiceTest {
    @Autowired
    private DeviceService deviceService;
    @Test
    public void successToGetDevices() {
        assertTrue(
                deviceService.getDevices() != null && deviceService.getDevices().size() == 5
        );
    }

}
