package com.example.demo.controllers;

import com.example.demo.entities.Role;
import com.example.demo.entities.RoleName;
import com.example.demo.entities.UserEntity;
import com.example.demo.security.util.JwtTokenUtil;
import com.example.demo.services.EmailService;
import com.example.demo.services.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam("firstName") String firstName,
                                                          @RequestParam("lastName") String lastName,
                                                          @RequestParam("email") String email,
                                                          @RequestParam("password") String password,
                                                          @RequestParam("confirmPassword") String confirmPassword,
                                                          @RequestParam("phoneNumber") String phoneNumber,
                                                          @RequestParam("role") String role, // Role as a String
                                                          @RequestParam(value = "image", required = false) MultipartFile image) {
        // Validate required fields
        if (firstName == null || lastName == null || email == null || password == null || phoneNumber == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "All fields must be provided"));
        }

        // Validate if passwords match
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Passwords do not match"));
        }

        try {
            // Generate username
            String username = firstName.substring(0, 2) + lastName.substring(0, 2);

            // Create the Role from the RoleName enum
            RoleName roleName = RoleName.valueOf(role.toUpperCase()); // Convert the string to RoleName enum
            Role userRole = new Role();
            userRole.setRoleName(roleName);

            // Hash the password before saving
            String hashedPassword = passwordEncoder.encode(password); // Assuming you have a passwordEncoder bean

            // Create user entity
            UserEntity user = new UserEntity();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(hashedPassword); // Save the hashed password
            user.setPhoneNumber(phoneNumber);
            user.setUsername(username);
            user.setRoles(Arrays.asList(userRole)); // Set the roles (with the Role object)

            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                // Save the image file (image saving logic goes here)
                String imagePath = saveImage(image);
                user.setProfileImage(imagePath); // Assuming you add an image field in the UserEntity
            }

            // Save user to database
            UserEntity createdUser = userService.createUser(user);

            // Send a welcome email (email sending logic goes here)
            sendWelcomeEmail(user);

            // Prepare the response body with user data (but no token generated here)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User successfully added");
            response.put("user", createdUser);

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
                + "Username: " + user.getUsername() + "\n"
                + "Password: " + user.getPassword(); // In real-world applications, don't send passwords in plain text

        emailService.sendEmail(user.getEmail(), subject, body); // Sending email
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
