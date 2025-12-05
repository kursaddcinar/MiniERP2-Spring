package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.DtoCariAccount;
import com.kursaddcinar.minierp.entity.CariAccount;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.jwt.dto.DtoRole;
import com.kursaddcinar.minierp.jwt.entity.Role;
import com.kursaddcinar.minierp.repository.RoleRepository;
import com.kursaddcinar.minierp.service.IRoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok ile otomatik Logger (private final Logger log...)
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    
    @Override
    public ApiResponse<Boolean> existByName(String name){
    	Role role = roleRepository.findByName(name)
    			.orElseThrow(()-> new ResourceNotFoundException("Role Bulunamadı : "+ name));

    	return ApiResponse.success(true);
    }
    
    @Override
    public ApiResponse<DtoRole> findByName(String name){
    	Role role = roleRepository.findByName(name)
    			.orElseThrow(()-> new ResourceNotFoundException("Role Bulunamadı : "+ name));
    	return ApiResponse.success(mapToDtoRole(role));
    }
    
    private DtoRole mapToDtoRole(Role entity) {
    	DtoRole dto = new DtoRole();
    	dto.setActive(entity.isActive());
    	dto.setDescription(entity.getDescription());
    	dto.setRoleId(entity.getRoleId());
    	dto.setRoleName(entity.getRoleName());
        return dto;
    }
}