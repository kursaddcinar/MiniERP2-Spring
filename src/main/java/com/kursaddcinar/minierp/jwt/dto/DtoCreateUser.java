package com.kursaddcinar.minierp.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class DtoCreateUser {
    @NotBlank(message = "Kullanıcı adı zorunludur")
    private String username;
    
    @NotBlank(message = "Şifre zorunludur")
    private String password;
    
    private String email;
    private String firstName;
    private String lastName;
    private List<Integer> roleIds;
}