package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.dto.DtoLogin;
import com.kursaddcinar.minierp.dto.DtoCreateUser;
import com.kursaddcinar.minierp.dto.DtoLoginResponse; // Token dönecek DTO
import com.kursaddcinar.minierp.dto.DtoUser;
import com.kursaddcinar.minierp.service.IAuthService;
import com.kursaddcinar.minierp.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ApiResponse<DtoUser> register(@RequestBody DtoCreateUser request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<DtoLoginResponse> login(@RequestBody DtoLogin request) {
        return authService.login(request);
    }
}