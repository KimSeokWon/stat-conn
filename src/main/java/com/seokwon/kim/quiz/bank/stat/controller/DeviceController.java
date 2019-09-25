package com.seokwon.kim.quiz.bank.stat.controller;

import com.seokwon.kim.quiz.bank.stat.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/code-device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices")
    public @ResponseBody
    List getDevices() {
        return this.deviceService.getDevices();
    }
}
