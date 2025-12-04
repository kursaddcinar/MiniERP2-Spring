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
import com.kursaddcinar.minierp.security.CustomUserDetailsService;
import com.kursaddcinar.minierp.security.JwtUtil;
import com.kursaddcinar.minierp.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<DtoLoginResponse> login(DtoLogin loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Kullanıcı detaylarını çek
            User user = userRepository.findByUsername(loginDto.getUsername()).orElseThrow();
            
            // Last Login Date güncelle
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            // Token üret
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            List<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).collect(Collectors.toList());
            String token = jwtUtil.generateToken(userDetails, user.getUserId(), roles);

            DtoLoginResponse response = new DtoLoginResponse();
            response.setToken(token);
            
            DtoUser userDto = new DtoUser();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            userDto.setRoles(roles);
            // ... diğer alanlar
            
            response.setUser(userDto);
            
            return ApiResponse.success(response, "Giriş başarılı.");
        } catch (Exception e) {
            throw new BusinessRuleException("Kullanıcı adı veya şifre hatalı.");
        }
    }

    @Override
    @Transactional
    public ApiResponse<DtoUser> register(DtoCreateUser createUserDto) {
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new BusinessRuleException("Bu kullanıcı adı alınmış.");
        }

        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        
        // Default Role: USER (Eğer DB'de varsa ata)
        roleRepository.findByRoleName("User").ifPresent(role -> {
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        });

        return ApiResponse.success(null, "Kayıt başarılı. Lütfen giriş yapın.");
    }

    @Override
    public ApiResponse<String> refreshToken(String token) {
        // Refresh token mantığı burada (Basit versiyonda yeniden login istenir)
        return null;
    }

    @Override
    public ApiResponse<Boolean> changePassword(Integer userId, DtoChangePassword changePasswordDto) {
        User user = userRepository.findById(userId).orElseThrow();
        
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Mevcut şifre hatalı.");
        }
        
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        return ApiResponse.success(true, "Şifre değiştirildi.");
    }

    @Override
    public ApiResponse<DtoUser> getCurrentUser(Integer userId) {
        // UserServiceImpl'deki getById ile aynı mantık
        return null; 
    }

    @Override
    public ApiResponse<Boolean> logout(Integer userId) {
        // Stateless JWT'de logout client side'da token silinerek yapılır.
        return ApiResponse.success(true, "Çıkış yapıldı.");
    }

    @Override
    public ApiResponse<Boolean> validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return ApiResponse.success(jwtUtil.validateToken(token, userDetails));
        } catch (Exception e) {
            return ApiResponse.success(false);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Object> initializeTestUsers() {
        // Admin Rolü
        Role adminRole = roleRepository.findByRoleName("Admin").orElseGet(() -> {
            Role r = new Role(); r.setRoleName("Admin"); return roleRepository.save(r);
        });
        
        // Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("System");
            admin.setLastName("Admin");
            User saved = userRepository.save(admin);
            
            UserRole ur = new UserRole();
            ur.setUser(saved);
            ur.setRole(adminRole);
            userRoleRepository.save(ur);
        }
        
        return ApiResponse.success(true, "Test verileri yüklendi.");
    }
}