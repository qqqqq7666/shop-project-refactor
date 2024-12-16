package com.example.shop_project.jwt;

import java.util.Date;
import java.util.List;
import java.lang.IllegalArgumentException;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
// JWT를 생성하고 검증하는 역할을 하는 클래스
@Component
public class JwtTokenProvider {	
    private final long TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간 (밀리초)
    
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
   
    // 토큰 생성
    public String generateToken(String name, List<String> roles) {
    	Claims claims = Jwts.claims().setSubject(name);
    	claims.put("roles", roles);
    	
    	return Jwts.builder()
    			.setClaims(claims)
    			.setIssuedAt(new Date())
    			.setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
    			.signWith(key)
    			.compact();
    }
    
    public Claims getClaimsFromToken(String token) {
    	return Jwts.parserBuilder()		// jwt 파싱을 위한 빌더 객체 생성
    			.setSigningKey(key)		// jwt서명을 검증하기 위한 키 생성
    			.build()				
    			.parseClaimsJws(token)	// 전달된 jwt 토큰 파싱, 서명을 검증하고 유효하면 페이로드 정보 반환
    			.getBody();				// jwt의 페이로드 부분을 가져옴
    }
    
    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() 		// 새로운 파서 빌더 생성
                .setSigningKey(key) 	// 서명 키 설정
                .build() 				// 파서 빌드
                .parseClaimsJws(token); // 토큰 검증 및 파싱
            return true; 				// 유효하면 true 반환
        } catch (JwtException | IllegalArgumentException e) {	// JWT 서명 검증 실패 또는 파싱 오류
            return false;
        }
    }
}
