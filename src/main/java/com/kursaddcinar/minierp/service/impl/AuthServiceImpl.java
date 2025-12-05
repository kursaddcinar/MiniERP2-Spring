package com.kursaddcinar.minierp.service.impl;

import com.kursaddcinar.minierp.common.ApiResponse;
import com.kursaddcinar.minierp.dto.*;
import com.kursaddcinar.minierp.entity.Role;
import com.kursaddcinar.minierp.entity.User;
import com.kursaddcinar.minierp.entity.UserRole;
import com.kursaddcinar.minierp.exception.BusinessRuleException;
import com.kursaddcinar.minierp.repository.RoleRepository;
import com.kursaddcinar.minierp.repository.UserRepository;
import com.kursaddcinar.minierp.repository.UserRoleRepository;
import com.kursaddcinar.minierp.security.CustomUserDetailsService;
import com.kursaddcinar.minierp.security.JwtUtil;
import com.kursaddcinar.minierp.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
            // 1. Spring Security Authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 2. Kullanıcıyı getir
            User user = userRepository.findByUsername(loginDto.getUsername())
                    .orElseThrow(() -> new BusinessRuleException("Kullanıcı bulunamadı."));
            
            // 3. Last Login güncelle
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            // 4. Token Üret
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            List<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getRoleName())
                    .collect(Collectors.toList());
                    
            String token = jwtUtil.generateToken(userDetails, user.getUserId(), roles);

            // 5. Response Hazırla
            DtoLoginResponse response = new DtoLoginResponse();
            response.setToken(token);
            
            DtoUser userDto = new DtoUser();
            userDto.setUserId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            userDto.setFirstName(user.getFirstName()); 
            userDto.setLastName(user.getLastName());   
            userDto.setRoles(roles);
            
            response.setUser(userDto);
            
            return ApiResponse.success(response, "Giriş başarılı.");
            
        } catch (BadCredentialsException e) {
            throw new BusinessRuleException("Kullanıcı adı veya şifre hatalı.");
        } catch (Exception e) {
            log.error("Login hatası: ", e);
            throw new BusinessRuleException("Giriş sırasında beklenmedik bir hata oluştu: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse<DtoUser> register(DtoCreateUser createUserDto) {
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new BusinessRuleException("Bu kullanıcı adı zaten kullanımda.");
        }

        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setEmail(createUserDto.getEmail());
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new BusinessRuleException("Sistemde ROLE_USER tanımı bulunamadı."));

        UserRole relation = new UserRole();
        relation.setUser(savedUser);
        relation.setRole(userRole);
        userRoleRepository.save(relation);

        // Şimdilik null dönüyoruz
        return ApiResponse.success(null, "Kayıt başarılı. Giriş yapabilirsiniz.");
    }

    @Override
    public ApiResponse<DtoUser> getCurrentUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("Kullanıcı bulunamadı."));
        
        DtoUser dto = new DtoUser();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList()));
                
        return ApiResponse.success(dto);
    }
    
    @Override
    public ApiResponse<String> refreshToken(String token) { return null; }

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
    public ApiResponse<Boolean> logout(Integer userId) {
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
        // Sistemde olması gereken tüm rollerin listesi
        String[] roles = {
            "ROLE_ADMIN", 
            "ROLE_MANAGER", 
            "ROLE_PURCHASE", 
            "ROLE_FINANCE", 
            "ROLE_SALES", 
            "ROLE_WAREHOUSE",
            "ROLE_USER" // Standart kullanıcı rolünü de ekleyelim
        };

        // 1. Rolleri veritabanında oluştur veya bul
        List<Role> allRoles = new ArrayList<>();
        for (String roleName : roles) {
            Role role = roleRepository.findByRoleName(roleName).orElseGet(() -> {
                Role newRole = new Role();
                newRole.setRoleName(roleName);
                return roleRepository.save(newRole);
            });
            allRoles.add(role);
        }
        
        // 2. Admin kullanıcısını oluştur ve TÜM rolleri ata
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Şifre: admin123
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEmail("admin@minierp.com");
            admin.setActive(true);
            
            User savedAdmin = userRepository.save(admin);
            
            // Admin'e listedeki bütüm rolleri veriyoruz
            for (Role role : allRoles) {
                UserRole ur = new UserRole();
                ur.setUser(savedAdmin);
                ur.setRole(role);
                userRoleRepository.save(ur);
            }
            
            log.info("Admin kullanıcısı oluşturuldu ve tüm yetkiler atandı: admin / admin123");
        }
        
        return ApiResponse.success(true, "Tüm roller ve test verileri yüklendi.");
    }
}