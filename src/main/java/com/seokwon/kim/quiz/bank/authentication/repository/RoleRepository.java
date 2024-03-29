package com.seokwon.kim.quiz.bank.authentication.repository;

import com.seokwon.kim.quiz.bank.authentication.model.Role;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, ObjectId> {
    Optional<Role> findByRoleName(Role.RoleName roleName);
}
