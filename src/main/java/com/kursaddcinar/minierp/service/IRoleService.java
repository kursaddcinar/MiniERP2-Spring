package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.jwt.dto.DtoRole;

public interface IRoleService {    
    ApiResponse<Boolean> existByName(String name);
    ApiResponse<DtoRole> findByName(String name);
}