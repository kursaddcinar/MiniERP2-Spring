package com.kursaddcinar.minierp.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kursaddcinar.minierp.jwt.dto.DtoUser;
import com.kursaddcinar.minierp.jwt.entity.AuthRequest;
import com.kursaddcinar.minierp.jwt.entity.AuthResponse;
import com.kursaddcinar.minierp.jwt.entity.RefreshTokenRequest;
import com.kursaddcinar.minierp.jwt.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	@PostMapping("/register")
	public DtoUser register(@Valid @RequestBody AuthRequest request) {
		return authService.register(request);
	}

	@PostMapping("/authenticate")
	public AuthResponse authenticate(@Valid @RequestBody AuthRequest request) {
		return authService.authenticate(request);
	}

	@PostMapping("/refreshToken")
	public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
		return refreshTokenService.refreshToken(request);
	}

	
}
