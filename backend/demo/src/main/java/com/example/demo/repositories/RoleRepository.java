package com.example.demo.repositories;

import com.example.demo.entities.Role;
import com.example.demo.entities.RoleName;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRoleName(RoleName roleName);
}


