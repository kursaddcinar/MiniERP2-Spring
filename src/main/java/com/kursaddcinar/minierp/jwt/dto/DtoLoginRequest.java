package com.kursaddcinar.minierp.jwt.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}