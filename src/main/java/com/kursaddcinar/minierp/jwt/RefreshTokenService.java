package com.kursaddcinar.minierp.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kursaddcinar.minierp.jwt.entity.RefreshTokenRequest;
import com.kursaddcinar.minierp.jwt.entity.AuthResponse;
import com.kursaddcinar.minierp.jwt.entity.RefreshToken;
import com.kursaddcinar.minierp.jwt.entity.User;

@Service
public class RefreshTokenService {
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	public boolean isRefreshTokenExpired(Date expiredDate) {
		return new Date().before(expiredDate);
	}
	
	private RefreshToken createRefreshToken(User user) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setRefreshToken(UUID.randomUUID().toString());
		refreshToken.setExpireDate(new Date(System.currentTimeMillis()+ 1000*60*60*4));
		refreshToken.setUser(user);
		
		return refreshToken;
	}

	//sjkfaskf ksjf askjf aksjf kjsldfkjl
	public AuthResponse refreshToken(RefreshTokenRequest request) {
		Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(request.getRefreshToken());
		if(optional.isEmpty()) {
			System.out.println("REFRESH TOKEN GEÇERSİZDİR : " + request.getRefreshToken());
		}
		
		RefreshToken refreshToken = optional.get();
		
		if(!isRefreshTokenExpired(refreshToken.getExpireDate())) {
			System.out.println("REFRESH TOKEN EXPİRE OLMUŞTUR BABA : " + request.getRefreshToken());
		}
		
		String accessToken = jwtService.generateToken(refreshToken.getUser());
		RefreshToken savedRefreshToken= refreshTokenRepository.save(createRefreshToken(refreshToken.getUser()));
		
		// accesss 2
		// refresh 1
		
		return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken());
	}

}
