package com.wycherley.trackmybus.models;

public class Student {
    private String studentId;
    private String name;
    private String grade;
    private String parentId;
    private String assignedBusId;
    private String pickupPointId;
    private String dropoffPointId;
    private String profileImageUrl;
    private boolean isActive;

    // No-argument constructor (required for Firebase)
    public Student() {
        this.studentId = "";
        this.name = "";
        this.grade = "";
        this.parentId = "";
        this.assignedBusId = "";
        this.pickupPointId = "";
        this.dropoffPointId = "";
        this.profileImageUrl = "";
        this.isActive = true;
    }

    // Constructor with parameters
    public Student(String studentId, String name, String grade) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.parentId = "";
        this.assignedBusId = "";
        this.pickupPointId = "";
        this.dropoffPointId = "";
        this.profileImageUrl = "";
        this.isActive = true;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(String assignedBusId) { this.assignedBusId = assignedBusId; }

    public String getPickupPointId() { return pickupPointId; }
    public void setPickupPointId(String pickupPointId) { this.pickupPointId = pickupPointId; }

    public String getDropoffPointId() { return dropoffPointId; }
    public void setDropoffPointId(String dropoffPointId) { this.dropoffPointId = dropoffPointId; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}