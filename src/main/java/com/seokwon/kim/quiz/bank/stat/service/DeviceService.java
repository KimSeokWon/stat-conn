package com.seokwon.kim.quiz.bank.stat.service;

import com.seokwon.kim.quiz.bank.stat.model.Device;
import com.seokwon.kim.quiz.bank.stat.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Autowired
    DeviceService(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }
}
