package com.kursaddcinar.minierp.controller;

import com.kursaddcinar.minierp.dto.DtoUser;
import com.kursaddcinar.minierp.service.IUserService;
import com.kursaddcinar.minierp.common.ApiResponse; // Senin ApiResponse yapın
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // Belirli bir role sahip kullanıcıları getirir
    // Örnek istek: GET /api/users/role/ADMIN
    @GetMapping("/role/{roleName}")
    public ApiResponse<List<DtoUser>> getUsersByRole(@PathVariable String roleName) {
        return userService.getUsersByRole(roleName);
    }
}