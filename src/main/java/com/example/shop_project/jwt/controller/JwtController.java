package com.example.shop_project.jwt.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.jwt.AuthTokenImpl;
import com.example.shop_project.jwt.JwtProvider;
import com.example.shop_project.jwt.dto.JwtTokenDto;
import com.example.shop_project.jwt.dto.JwtTokenLoginRequest;
import com.example.shop_project.jwt.dto.JwtTokenResponse;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JwtController {

    private final MemberService userService;
    private final JwtProvider jwtProvider;
    
    @PostMapping("/jwt-login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(@RequestBody JwtTokenLoginRequest request,
                                                     HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.login(request);

        // -> Access Token, Refresh Token 생성

        // 1) Access Token 쿠키로 저장
        Cookie accessTokenCookie = new Cookie("accessToken", jwtTokenDto.getAccessToken());
        accessTokenCookie.setHttpOnly(true);   // 자바스크립트 접근 방지
        accessTokenCookie.setSecure(false);     // HTTPS에서만 전송
        accessTokenCookie.setPath("/");        // 모든 경로에서 쿠키 전송
        accessTokenCookie.setMaxAge(10 * 60);  // 예: 만료시간 10분(초 단위)

        // 2) Refresh Token 쿠키로 저장 (이미 구현된 부분과 동일)
        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtTokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60); // 예: 2주

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // csrf 공격을 막기 위한 samesite 설정
        response.setHeader("Set-Cookie", String.format("accessToken=%s; Path=/; HttpOnly; Secure; SameSite=Lax", jwtTokenDto.getAccessToken()));
        response.addHeader("Set-Cookie", String.format("refreshToken=%s; Path=/; HttpOnly; Secure; SameSite=Lax", jwtTokenDto.getRefreshToken()));
        
        // 3) 추가로 클라이언트에 AccessToken을 JSON으로 줄 필요가 없다면, 
        //    JwtTokenResponse 대신 간단한 성공 메시지를 내려줘도 됨.
        return ResponseEntity.ok(
            JwtTokenResponse.builder()
            .accessToken(jwtTokenDto.getAccessToken())
            .build()
        );
    }
    
    @PostMapping("/jwt-logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // HTTPS 환경이 아니므로 false

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 성공");
    }
    
    @PostMapping("/jwt-refresh")
    public ResponseEntity<JwtTokenResponse> refreshAccessToken(HttpServletRequest request,
    	    													HttpServletResponse response) {
        // 1. Refresh Token 추출
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Refresh Token 검증
        AuthTokenImpl refreshTokenObj = (AuthTokenImpl) jwtProvider.convertAuthToken(refreshToken);
        if (!refreshTokenObj.validate()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 3. 새로운 Access Token 생성
        String email = refreshTokenObj.getDate().getSubject();
        Member member = userService.findByEmail(email);

        AuthTokenImpl accessToken = (AuthTokenImpl)jwtProvider.createAccessToken(
            member.getEmail(),
            member.getRole(),
            null
        );

        String newAccessToken = accessToken.getToken();

        // 4. Access Token을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(10 * 60); // 10분

        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(
            JwtTokenResponse.builder()
                .accessToken(newAccessToken)
                .build()
        );
    }


}
