package com.seokwon.kim.quiz.bank.authentication.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "USER_ROLE")
@Data
public class Role {
    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN,
    };
    @Id
    private ObjectId id;

    @Indexed
    private RoleName roleName;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }
}
