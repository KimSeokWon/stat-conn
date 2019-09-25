package com.seokwon.kim.quiz.bank.repository;

import com.seokwon.kim.quiz.bank.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
}
