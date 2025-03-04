package com.example.demo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String CIN;
    private String phoneNumber;
    private String email;
    private String profileImage;  // Added field to store the image path or URL

    @DBRef
    private List<Role> roles = new ArrayList<>();  // Default roles list initialization

    public UserEntity() {}

    public UserEntity(String firstName, String lastName, String username, String password, String CIN, String phoneNumber, String email, String profileImage) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = generateUsername(firstName, lastName);  // Generate username
        this.password = password;
        this.CIN = CIN;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.profileImage = profileImage;
    }

    private String generateUsername(String firstName, String lastName) {
        return (firstName.length() > 1 ? firstName.substring(0, 2).toLowerCase() : "") +
                (lastName.length() > 1 ? lastName.substring(0, 2).toLowerCase() : "");
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCIN() {
        return CIN;
    }

    public void setCIN(String CIN) {
        this.CIN = CIN;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
