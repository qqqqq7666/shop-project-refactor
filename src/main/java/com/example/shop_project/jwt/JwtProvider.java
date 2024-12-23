package com.example.shop_project.jwt;

import java.util.Map;

import org.springframework.security.core.Authentication;

import com.example.shop_project.member.Role;

public interface JwtProvider <T> {
	
	T convertAuthToken(String token);
	
	Authentication getAuthentication(T authToken);
	
	T createAccessToken(String userId, Role role, Map<String, Object> claims);

	T createRefreshToken(String userId, Role role, Map<String, Object> claims);
}
