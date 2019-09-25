package com.seokwon.kim.quiz.bank.stat.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CONNECTION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connection {
    @Id
    private String          id;
    @Indexed
    private int             year;
    private double          useRate;
    @Indexed
    private String  device_id;
    private String  device_name;
    @Indexed
    private double  rate;
}
