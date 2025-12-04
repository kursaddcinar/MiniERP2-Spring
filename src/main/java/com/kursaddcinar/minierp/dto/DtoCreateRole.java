package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoCreateRole {
    @NotBlank
    private String roleName;
    private String description;
}