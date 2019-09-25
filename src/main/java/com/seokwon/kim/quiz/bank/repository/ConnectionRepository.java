package com.seokwon.kim.quiz.bank.repository;

import com.seokwon.kim.quiz.bank.model.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends MongoRepository<Connection, String> {
}
