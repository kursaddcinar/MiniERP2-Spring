package com.kursaddcinar.minierp.jwt;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
	

	
	// application.properties'den okuyacağız,
	@Value("${application.security.jwt.secret-key}")
    private String secretKey;
	
	@Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
	
	public String generateToken(UserDetails userDetails) {
		Map<String, String> claimsMap	=	new HashMap<>();
		claimsMap.put("role","admin");
		
		return	Jwts.builder()
		.setSubject(userDetails.getUsername())
		.setIssuedAt(new Date())
		.setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*2))//mlsn cinsinden ekleme yapılıyor 100mlsn = 1sn
		.signWith(getKey(),SignatureAlgorithm.HS256)
		.compact();
	}
	
	public <T> T exportToken(String token , Function<Claims, T> claimsFunction) {
		Claims claims	=	Jwts
		.parserBuilder()
		.setSigningKey(getKey())
		.build()
		.parseClaimsJws(token).getBody();
		
		return claimsFunction.apply(claims);
	}
	
	public String getUserNameByToken(String token) {
		return	exportToken(token, Claims::getSubject);
	}
	
	public Key getKey() {
		//byte[] keyBytes	=	Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
	
	public boolean isTokenExpired(String token) {
		Date expireDate = exportToken(token, Claims::getExpiration);
		return new Date().before(expireDate);
	}
}
