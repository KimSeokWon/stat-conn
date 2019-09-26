package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "USER")
@Getter @Setter
public class User extends DataAudit {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private Set<Role> roles = new HashSet<>();

    public User(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
}
