package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;

public interface IAuthService {
    ApiResponse<DtoLoginResponse> login(DtoLogin loginDto);
    ApiResponse<DtoUser> register(DtoCreateUser createUserDto);
    ApiResponse<String> refreshToken(String token);
    ApiResponse<Boolean> changePassword(Integer userId, DtoChangePassword changePasswordDto);
    ApiResponse<DtoUser> getCurrentUser(Integer userId);
    ApiResponse<Boolean> logout(Integer userId);
    ApiResponse<Boolean> validateToken(String token);
    ApiResponse<Object> initializeTestUsers();
}