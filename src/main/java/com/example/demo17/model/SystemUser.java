package com.example.demo17.model;

import java.time.LocalDate;

public class SystemUser {
    private int       userId;
    private String    username;
    private String    password;
    private String    accessCode;
    private String    role;
    private String    access;
    private LocalDate createdAt;

    public SystemUser() {}

    public SystemUser(int userId, String username, String password,
                      String accessCode, String role, String access, LocalDate createdAt) {
        this.userId     = userId;
        this.username   = username;
        this.password   = password;
        this.accessCode = accessCode;
        this.role       = role;
        this.access     = access;
        this.createdAt  = createdAt;
    }

    public int       getUserId()               { return userId; }
    public void      setUserId(int v)          { this.userId = v; }
    public String    getUsername()             { return username; }
    public void      setUsername(String v)     { this.username = v; }
    public String    getPassword()             { return password; }
    public void      setPassword(String v)     { this.password = v; }
    public String    getAccessCode()           { return accessCode; }
    public void      setAccessCode(String v)   { this.accessCode = v; }
    public String    getRole()                 { return role; }
    public void      setRole(String v)         { this.role = v; }
    public String    getAccess()               { return access; }
    public void      setAccess(String v)       { this.access = v; }
    public LocalDate getCreatedAt()            { return createdAt; }
    public void      setCreatedAt(LocalDate v) { this.createdAt = v; }

    @Override
    public String toString() {
        return username + " [" + role + "] - " + access;
    }
}