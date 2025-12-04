package com.kursaddcinar.minierp.dto;

import lombok.Data;

@Data
public class DtoRole {
    private Integer roleId;
    private String roleName;
    private String description;
    private boolean isActive;
}