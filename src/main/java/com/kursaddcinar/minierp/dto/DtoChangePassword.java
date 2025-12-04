package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoChangePassword {
    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
}