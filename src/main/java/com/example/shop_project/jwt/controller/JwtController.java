package com.example.shop_project.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.jwt.dto.JwtTokenDto;
import com.example.shop_project.jwt.dto.JwtTokenLoginRequest;
import com.example.shop_project.jwt.dto.JwtTokenResponse;
import com.example.shop_project.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JwtController {

    private final MemberService userService;

    @PostMapping("/jwt-login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(
        @RequestBody JwtTokenLoginRequest request,
        HttpServletResponse response
    ) {
        JwtTokenDto jwtTokenResponse = userService.login(request);

        Cookie refreshTokenCookie = new Cookie(
            "refreshToken",
            jwtTokenResponse.getRefreshToken()
        );

        // 설정 값 추가
        refreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        refreshTokenCookie.setSecure(true); // HTTPS에서만 쿠키 전송
        refreshTokenCookie.setPath("/"); // 쿠키 유효 경로 설정
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간: 7일

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok()
            .body(JwtTokenResponse.builder()
                .accessToken(jwtTokenResponse.getAccessToken())
                .build()
            );
    }
}
