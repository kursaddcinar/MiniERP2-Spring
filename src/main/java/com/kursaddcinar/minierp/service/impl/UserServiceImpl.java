package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.Role;
import com.kursaddcinar.minierp.entity.User;
import com.kursaddcinar.minierp.entity.UserRole;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.exception.ResourceNotFoundException;
import com.kursaddcinar.minierp.repository.RoleRepository;
import com.kursaddcinar.minierp.repository.UserRepository;
import com.kursaddcinar.minierp.repository.UserRoleRepository;
import com.kursaddcinar.minierp.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<Page<DtoUser>> getUsers(Pageable pageable) {
        return ApiResponse.success(userRepository.findAll(pageable).map(this::mapToDto));
    }

    @Override
    public ApiResponse<DtoUser> getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + id));
        return ApiResponse.success(mapToDto(user));
    }

    @Override
    @Transactional
    public ApiResponse<DtoUser> createUser(DtoCreateUser dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessRuleException("Kullanıcı adı zaten var.");
        }
        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("Email zaten var.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setActive(true);
        
        User savedUser = userRepository.save(user);

        // Rolleri atama
        if (dto.getRoleIds() != null) {
            assignRoles(savedUser, dto.getRoleIds());
        }

        return ApiResponse.success(mapToDto(savedUser), "Kullanıcı oluşturuldu.");
    }

    @Override
    @Transactional
    public ApiResponse<DtoUser> updateUser(Integer id, DtoUpdateUser dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setActive(dto.isActive());

        if (dto.getRoleIds() != null) {
            // Öncekileri sil
            userRoleRepository.deleteByUserUserId(id);
            // Yenileri ekle
            assignRoles(user, dto.getRoleIds());
        }

        User updated = userRepository.save(user);
        return ApiResponse.success(mapToDto(updated), "Kullanıcı güncellendi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
        return ApiResponse.success(true, "Kullanıcı silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> activateUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(true);
        userRepository.save(user);
        return ApiResponse.success(true, "Kullanıcı aktif edildi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deactivateUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        return ApiResponse.success(true, "Kullanıcı pasife alındı.");
    }

    @Override
    public ApiResponse<List<DtoUser>> getUsersByRole(String roleName) {
        // Repository'den veriyi çek
        List<User> userList = userRepository.findByRoles_Name(roleName);

        // Eğer liste boşsa veya rol bulunamazsa yine de success dönebiliriz (boş liste ile),
        // ama kullanıcıya bilgi vermek istersen check koyabilirsin.
        // Biz şimdilik dönüşümü yapıp veriyoruz.
        
        List<DtoUser> dtoUserList = userList.stream()
                .map(user -> {
                    DtoUser dto = new DtoUser();
                    // BeanUtils.copyProperties(user, dto); veya manuel set:
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    // ... diğer alanlar
                    return dto;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(dtoUserList);
    }

    // --- ROLE OPERATIONS ---

    @Override
    public ApiResponse<List<DtoRole>> getRoles() {
        List<Role> roles = roleRepository.findAll();
        return ApiResponse.success(roles.stream().map(this::mapToDtoRole).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ApiResponse<DtoRole> createRole(DtoCreateRole dto) {
        if (roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new BusinessRuleException("Rol zaten var.");
        }
        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());
        role.setActive(true);
        return ApiResponse.success(mapToDtoRole(roleRepository.save(role)));
    }

    @Override
    @Transactional
    public ApiResponse<DtoRole> updateRole(Integer id, DtoUpdateRole dto) {
        Role role = roleRepository.findById(id).orElseThrow();
        role.setDescription(dto.getDescription());
        role.setActive(dto.isActive());
        return ApiResponse.success(mapToDtoRole(roleRepository.save(role)));
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteRole(Integer id) {
        Role role = roleRepository.findById(id).orElseThrow();
        roleRepository.delete(role);
        return ApiResponse.success(true, "Rol silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> assignRolesToUser(Integer userId, List<Integer> roleIds) {
        User user = userRepository.findById(userId).orElseThrow();
        userRoleRepository.deleteByUserUserId(userId);
        assignRoles(user, roleIds);
        return ApiResponse.success(true, "Roller atandı.");
    }

    // --- HELPERS ---

    private void assignRoles(User user, List<Integer> roleIds) {
        for (Integer roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı ID: " + roleId));
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    private DtoUser mapToDto(User user) {
        DtoUser dto = new DtoUser();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setActive(user.isActive());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setLastLoginDate(user.getLastLoginDate());
        
        // Rolleri string listesi olarak dön (Login olan kişinin yetkilerini görmek için)
        dto.setRoles(user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList()));
        return dto;
    }

    private DtoRole mapToDtoRole(Role role) {
        DtoRole dto = new DtoRole();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setActive(role.isActive());
        return dto;
    }
}