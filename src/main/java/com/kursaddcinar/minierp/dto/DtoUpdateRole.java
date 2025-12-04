package com.kursaddcinar.minierp.dto;

import lombok.Data;

@Data
public class DtoUpdateRole {
    private String description;
    private boolean isActive = true;
}