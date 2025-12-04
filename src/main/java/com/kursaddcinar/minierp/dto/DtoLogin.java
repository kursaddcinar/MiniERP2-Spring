package com.kursaddcinar.minierp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoLogin {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}