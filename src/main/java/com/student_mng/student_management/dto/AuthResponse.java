package com.student_mng.student_management.dto;

public class AuthResponse {

    String token;
    String userId;
    String role;

    public AuthResponse () {}

    public AuthResponse (String token, String userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
