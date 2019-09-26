package com.seokwon.kim.quiz.bank.stat.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor @Data
public class DeviceRate {
    private int year;
    private String device_id;
    private String device_name;
    private double rate;
}
