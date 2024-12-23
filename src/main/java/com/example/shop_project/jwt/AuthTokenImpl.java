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

    // 1. JWT 생성자
    public AuthTokenImpl(
        String userId,
        Role role,
        Key key,
        Map<String, Object> claimsMap,
        Date expiredDate
    ) {
        this.key = key;

        // Map 복사 및 DefaultClaims 변환
        Map<String, Object> modifiableMap = new HashMap<>();
        if (claimsMap != null) {
            modifiableMap.putAll(claimsMap);
        }

        DefaultClaims claims = new DefaultClaims(modifiableMap);
        claims.put(MemberConstants.AUTHORIZATION_TOKEN_KEY, role.getKey()); // role.getKey()로 String 권한 추가

        // JWT 생성
        this.token = Jwts.builder()
                .setSubject(userId)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiredDate)
                .compact();
    }

    // 2. 토큰 유효성 검증
    @Override
    public boolean validate() {
        return getDate() != null;
    }

    // 3. Claims 반환
    @Override
    public Claims getDate() {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(key) // 🔑 key를 직접 사용 (HMAC 키는 .getEncoded() 필요 없음)
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
