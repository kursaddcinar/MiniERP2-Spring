package com.kursaddcinar.minierp.service;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    ApiResponse<Page<DtoUser>> getUsers(Pageable pageable);
    ApiResponse<DtoUser> getUserById(Integer id);
    ApiResponse<DtoUser> createUser(DtoCreateUser createUserDto);
    ApiResponse<DtoUser> updateUser(Integer id, DtoUpdateUser updateUserDto);
    ApiResponse<Boolean> deleteUser(Integer id);
    ApiResponse<Boolean> activateUser(Integer id);
    ApiResponse<Boolean> deactivateUser(Integer id);
    ApiResponse<List<DtoUser>> getUsersByRole(String roleName);
    
    // Role Operations
    ApiResponse<List<DtoRole>> getRoles();
    ApiResponse<DtoRole> createRole(DtoCreateRole createRoleDto);
    ApiResponse<DtoRole> updateRole(Integer id, DtoUpdateRole updateRoleDto);
    ApiResponse<Boolean> deleteRole(Integer id);
    ApiResponse<Boolean> assignRolesToUser(Integer userId, List<Integer> roleIds);
}