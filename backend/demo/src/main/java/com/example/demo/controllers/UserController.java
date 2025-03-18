package com.example.demo.controllers;

import com.example.demo.dto.UserDTO;
import com.example.demo.entities.Role;
import com.example.demo.entities.RoleName;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.util.JwtTokenUtil;
import com.example.demo.services.EmailService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDTO userDTO) {
        // Validate required fields
        if (userDTO.getFirstName() == null || userDTO.getLastName() == null || userDTO.getEmail() == null ||
                userDTO.getPassword() == null || userDTO.getPhoneNumber() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "All fields must be provided"));
        }

        // Validate if passwords match
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Passwords do not match"));
        }

        // Validate role
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(userDTO.getRole().toUpperCase());  // Convert the string to RoleName enum
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid role provided"));
        }

        try {
            // Generate username
            String username = userDTO.getFirstName().substring(0, 2) + userDTO.getLastName().substring(0, 2);

            // Hash the password before saving
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

            // Create user entity
            UserEntity user = new UserEntity();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setPassword(hashedPassword);
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setUsername(username);

            // Save user to database first to generate the ID
            user = userRepository.save(user);

            // Create the Role object and assign it to the user
            Role userRole = new Role();
            userRole.setRoleName(roleName); // Role from request body
            userRole.setUser(user); // Set the user in the role

            // Save the role (role will now have a reference to the user)
            roleRepository.save(userRole);

            // Handle image upload if provided
            if (userDTO.getImage() != null && !userDTO.getImage().isEmpty()) {
                try {
                    String imagePath = saveImage(userDTO.getImage());  // Save the image
                    user.setProfileImage(imagePath);  // Save the image path in the user entity
                    userRepository.save(user);  // Update user with image path
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload image"));
                }
            }

            // Send welcome email
            sendWelcomeEmail(user);

            // Prepare the response body with user data
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User successfully added");
            response.put("user", user);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to create user"));
        }
    }

    // Method to save the image
    private String saveImage(MultipartFile image) throws IOException {
        Path path = Paths.get("uploads/" + image.getOriginalFilename());
        Files.write(path, image.getBytes());
        return path.toString(); // Return the saved image path
    }

    // Method to send a welcome email
    private void sendWelcomeEmail(UserEntity user) {
        String subject = "Welcome to Vermeg";
        String body = "Hello " + user.getFirstName() + ",\n\n"
                + "Welcome to Vermeg. Here are your details:\n\n"
                + "First Name: " + user.getFirstName() + "\n"
                + "Last Name: " + user.getLastName() + "\n"
                + "Phone Number: " + user.getPhoneNumber() + "\n"
                + "Username: " + user.getUsername() + "\n\n"
                + "Thank you for joining us!";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @PostMapping("/sendTestEmail")
    public ResponseEntity<String> sendTestEmail() {
        String to = "test@example.com";  // Use a valid test email address
        String subject = "Test Email";
        String body = "This is a test email!";
        emailService.sendEmail(to, subject, body);
        return ResponseEntity.ok("Test email sent successfully!");
    }



    // Endpoint to get all Users
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Endpoint to get a User by ID
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {
        Optional<UserEntity> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to update a User
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable String id, @RequestBody UserEntity updatedUser) {
        UserEntity user = userService.updateUser(id, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to delete a User by ID
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint for login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserEntity userEntity) {
        // Authenticate user and fetch the user object
        UserEntity authenticatedUser = userService.authenticateAndGenerateToken(userEntity.getUsername(), userEntity.getPassword());

        if (authenticatedUser != null) {
            // Generate JWT token for the authenticated user
            String jwtToken = jwtTokenUtil.generateToken(authenticatedUser.getUsername());

            // Prepare the response body with the token
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", jwtToken); // Add the token to the response
            return ResponseEntity.ok(response);
        } else {
            // If authentication fails, return an error
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
