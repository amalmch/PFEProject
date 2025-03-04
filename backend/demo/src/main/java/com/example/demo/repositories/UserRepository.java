package com.example.demo.repositories;

import com.example.demo.entities.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository <UserEntity,String> {
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
}