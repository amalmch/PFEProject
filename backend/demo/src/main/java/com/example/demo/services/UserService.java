package com.example.demo.services;

import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // For password encryption

    // Create user with encrypted password
    public UserEntity createUser(UserEntity user) {
        // Ensure password is encoded before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Update user
    public UserEntity updateUser(String id, UserEntity updatedUser) {
        if (userRepository.existsById(id)) {
            updatedUser.setId(id); // Ensure the ID is set for the updated object
            return userRepository.save(updatedUser);
        }
        return null; // Return null if the user with the given ID doesn't exist
    }

    // Delete user
    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get all users
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Find user by email
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find user by username
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Authenticate user
    public boolean authenticate(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    // Authenticate user and generate token if valid
    public UserEntity authenticateAndGenerateToken(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
}
