package com.example.shop_project.jwt;
import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.example.shop_project.member.Role;

import java.util.Collections;
import java.util.Set;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider<AuthTokenImpl> {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token.access-expires}")
    private long accessExpires;

    @Value("${jwt.token.refresh-expires}")
    private long refreshExpires;

    private Key key;

    @PostConstruct
    public void init() {
    	System.out.println("JWT Secret: " + secret); // 디버깅용 출력
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }
    
    @Override
    public AuthTokenImpl convertAuthToken(String token) {
        return new AuthTokenImpl(token, key);
    }

    @Override
    public Authentication getAuthentication(AuthTokenImpl authToken) {
        if (authToken.validate()) {
            Claims claims = authToken.getDate();

            if (!claims.get("type").equals(MemberConstants.ACCESS_TOKEN_TYPE_VALUE)) {
                throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token type"
                );
            }

            // 권한을 SimpleGrantedAuthority에 추가
            String roleKey = claims.get(MemberConstants.AUTHORIZATION_TOKEN_KEY, String.class);
            if (roleKey == null || roleKey.isEmpty()) {
                throw new JwtException("Role is missing in the token");
            }

            Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(roleKey)
            );

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(
                principal,
                authToken,
                authorities
            );
        } else {
            throw new JwtException("token Error");
        }
    }
    
    @Override
    public AuthTokenImpl createAccessToken(
        String userId,
        Role role,
        Map<String, Object> claims
    ) {
        if (claims == null) {
            claims = new HashMap<>();
        } else {
            claims = new HashMap<>(claims);
        }

        // 토큰 타입 및 권한 정보 추가
        claims.put("type", MemberConstants.ACCESS_TOKEN_TYPE_VALUE); // 타입 추가
        claims.put(MemberConstants.AUTHORIZATION_TOKEN_KEY, role.getKey()); // 권한 추가

        return new AuthTokenImpl(
            userId,
            role,
            key,
            claims,
            new Date(System.currentTimeMillis() + accessExpires)
        );
    }

    @Override
    public AuthTokenImpl createRefreshToken(
        String userId,
        Role role,
        Map<String, Object> claims
    ) {
        if (claims == null) {
            claims = new HashMap<>();
        } else {
            claims = new HashMap<>(claims);
        }

        // 토큰 타입 및 권한 정보 추가
        claims.put("type", MemberConstants.REFRESH_TOKEN_TYPE_VALUE); // 타입 추가
        claims.put(MemberConstants.AUTHORIZATION_TOKEN_KEY, role.getKey()); // 권한 추가

        return new AuthTokenImpl(
            userId,
            role,
            key,
            claims,
            new Date(System.currentTimeMillis() + refreshExpires)
        );
    }
}
