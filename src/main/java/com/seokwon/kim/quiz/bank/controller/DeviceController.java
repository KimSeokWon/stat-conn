package com.seokwon.kim.quiz.bank.controller;

import com.seokwon.kim.quiz.bank.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @GetMapping("/code-device/devices")
    public @ResponseBody
    List getDevices() {
        return this.deviceService.getDevices();
    }
}
