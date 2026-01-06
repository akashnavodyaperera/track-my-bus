package com.wycherley.trackmybus.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private UserRole role;
    private String profileImageUrl;
    private long createdAt;

    // Parent-specific fields
    private List<String> childrenIds;
    private List<String> assignedBusIds;

    // Driver-specific fields
    private String driverLicenseNumber;
    private String assignedBusId;

    // Admin-specific fields
    private String schoolId;
    private List<String> permissions;

    // No-argument constructor (required for Firebase)
    public User() {
        this.userId = "";
        this.email = "";
        this.name = "";
        this.phoneNumber = "";
        this.role = UserRole.PARENT;
        this.profileImageUrl = "";
        this.createdAt = System.currentTimeMillis();
        this.childrenIds = new ArrayList<>();
        this.assignedBusIds = new ArrayList<>();
        this.driverLicenseNumber = "";
        this.assignedBusId = "";
        this.schoolId = "";
        this.permissions = new ArrayList<>();
    }

    // Constructor with parameters
    public User(String userId, String email, String name, String phoneNumber, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.profileImageUrl = "";
        this.createdAt = System.currentTimeMillis();
        this.childrenIds = new ArrayList<>();
        this.assignedBusIds = new ArrayList<>();
        this.driverLicenseNumber = "";
        this.assignedBusId = "";
        this.schoolId = "";
        this.permissions = new ArrayList<>();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<String> getChildrenIds() { return childrenIds; }
    public void setChildrenIds(List<String> childrenIds) { this.childrenIds = childrenIds; }

    public List<String> getAssignedBusIds() { return assignedBusIds; }
    public void setAssignedBusIds(List<String> assignedBusIds) { this.assignedBusIds = assignedBusIds; }

    public String getDriverLicenseNumber() { return driverLicenseNumber; }
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    public String getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(String assignedBusId) { this.assignedBusId = assignedBusId; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}