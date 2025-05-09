package com.example.demo.controllers;

import com.example.demo.entities.Role;
import com.example.demo.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:4200") // Allow CORS for this controller
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<String>> getRoles() {
        // Fetch all roles from the service
        List<Role> roles = roleService.getAllRoles();

        // Use a Set to ensure unique role names
        List<String> roleNames = roles.stream()
                .map(role -> role.getRoleName().name())  // Convert RoleName enum to string
                .distinct()  // Ensure uniqueness
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleNames);
    }
}

