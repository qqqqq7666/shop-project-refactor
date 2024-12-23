package com.example.shop_project.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.shop_project.member.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.MalformedJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


@Getter
@AllArgsConstructor
@Slf4j
public class AuthTokenImpl implements AuthToken<Claims> {
	private final String token;
	private final Key key;
	
//	public AuthTokenImpl (
//			String userId,
//			Role role,
//			Key key,
//			Claims claims,
//			Date expiredDate
//	) {
//		this.key= key;
//		this.token = createJwtToken(userId, role, claims, expiredDate).get();
//	}
	
	public AuthTokenImpl(
            String userId,
            Role role,
            Key key,
            Map<String, Object> claimsMap, // ★ 수정: 파라미터를 Map<String, Object>로 받음
            Date expiredDate
    ) {
        this.key = key;
        // 여기서 DefaultClaims로 변환
        DefaultClaims claims = new DefaultClaims(claimsMap);
        claims.put(AUTHORITIES_TOKEN_KEY, role);

        // JWT 문자열 생성
        this.token = Jwts.builder()
                .setSubject(userId)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiredDate)
                .compact();
    }
	
	private Optional<String> createJwtToken(
			String userId,
			Role role,
			Map<String, Object> claimsMap,
			Date expiredDate
	) {
//		DefaultClaims claims = new DefaultClaims(claimsMap);
//		claims.put(AUTHORITIES_TOKEN_KEY, role);
//		
//		return Optional.ofNullable(Jwts.builder()
//				.setSubject(userId)
//				.addClaims(claims)
//				.signWith(key, SignatureAlgorithm.HS256)
//				.setExpiration(expiredDate)
//				.compact()
//		
//		);
		
		 // 불변 Map일 수도 있는 claimsMap을 복사해 변경 가능하게 만든다.
	    Map<String, Object> modifiableMap = new HashMap<>();
	    if (claimsMap != null) {
	        modifiableMap.putAll(claimsMap);
	    }

	    // DefaultClaims를 생성하기 전에 role을 넣어주거나,
	    // DefaultClaims를 만든 뒤 put해도 OK
	    DefaultClaims claims = new DefaultClaims(modifiableMap);

	    // 권한 정보를 추가
	    claims.put(AUTHORITIES_TOKEN_KEY, role);

	    return Optional.ofNullable(Jwts.builder()
	            .setSubject(userId)
	            .addClaims(claims)
	            .signWith(key, SignatureAlgorithm.HS256)
	            .setExpiration(expiredDate)
	            .compact()
	    );
	}
	
	@Override
	public boolean validate() {
		return getDate() != null;
	}
	
	@Override
	public Claims getDate() {
		try {
			return Jwts
					.parserBuilder()
					.setSigningKey(key.getEncoded())
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (SecurityException e) {
	        log.warn("Invalid JWT signature");
	    } catch (MalformedJwtException e) {
	        log.warn("Invalid JWT token");
	    } catch (ExpiredJwtException e) {
	        log.warn("Expired JWT token");
	    } catch (UnsupportedJwtException e) {
	        log.warn("Unsupported JWT Token");
	    } catch (IllegalArgumentException e) {
	        log.warn("JWT token compact of handler are invalid");
	    }
		
		return null;
	}
	
}
