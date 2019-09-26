package com.seokwon.kim.quiz.bank.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

@Getter @Setter
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"}
)
public abstract class DataAudit implements Serializable {
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedDate;

}
