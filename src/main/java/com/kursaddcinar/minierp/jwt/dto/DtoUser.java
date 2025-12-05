package com.kursaddcinar.minierp.jwt.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoUser {
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;
    private List<String> roles;
    
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
