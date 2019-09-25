package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "USER") @Data
public class User {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    private String password;
}
