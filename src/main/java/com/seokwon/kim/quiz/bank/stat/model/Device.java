package com.seokwon.kim.quiz.bank.stat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DEVICE")
@Data
@AllArgsConstructor
public class Device {
    @Id
    private String device_id;
    private String device_name;
}