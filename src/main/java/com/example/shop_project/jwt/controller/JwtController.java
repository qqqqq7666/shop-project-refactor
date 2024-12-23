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

//    @PostMapping("/jwt-login")
//    public ResponseEntity<JwtTokenResponse> jwtLogin(
//        @RequestBody JwtTokenLoginRequest request,
//        HttpServletResponse response
//    ) {
//        JwtTokenDto jwtTokenResponse = userService.login(request);
//
//        Cookie refreshTokenCookie = new Cookie(
//            "refreshToken",
//            jwtTokenResponse.getRefreshToken()
//        );
//
//        // 설정 값 추가
//        refreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
//        refreshTokenCookie.setSecure(true); // HTTPS에서만 쿠키 전송
//        refreshTokenCookie.setPath("/"); // 쿠키 유효 경로 설정
//        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효 기간: 7일
//
//        response.addCookie(refreshTokenCookie);
//
//        return ResponseEntity.ok()
//            .body(JwtTokenResponse.builder()
//                .accessToken(jwtTokenResponse.getAccessToken())
//                .build()
//            );
//    }
    
    @PostMapping("/jwt-login")
    public ResponseEntity<JwtTokenResponse> jwtLogin(@RequestBody JwtTokenLoginRequest request,
                                                     HttpServletResponse response) {

        JwtTokenDto jwtTokenDto = userService.login(request); 
        // -> Access Token, Refresh Token 생성

        // 1) Access Token 쿠키로 저장
        Cookie accessTokenCookie = new Cookie("accessToken", jwtTokenDto.getAccessToken());
        accessTokenCookie.setHttpOnly(true);   // 자바스크립트 접근 방지
        accessTokenCookie.setSecure(true);     // HTTPS에서만 전송
        accessTokenCookie.setPath("/");        // 모든 경로에서 쿠키 전송
        accessTokenCookie.setMaxAge(10 * 60);  // 예: 만료시간 10분(초 단위)

        // 2) Refresh Token 쿠키로 저장 (이미 구현된 부분과 동일)
        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtTokenDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60); // 예: 2주

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // 3) 추가로 클라이언트에 AccessToken을 JSON으로 줄 필요가 없다면, 
        //    JwtTokenResponse 대신 간단한 성공 메시지를 내려줘도 됨.
        return ResponseEntity.ok(
            JwtTokenResponse.builder()
            .accessToken(jwtTokenDto.getAccessToken())
            .build()
        );
    }

}
