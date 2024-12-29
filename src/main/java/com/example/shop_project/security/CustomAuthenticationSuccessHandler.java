package com.example.shop_project.security;

import com.example.shop_project.jwt.JwtProviderImpl;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtProviderImpl jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        boolean exists = memberRepository.findByEmail(email).isPresent();

        if (exists) {
            // 이미 DB에 존재하는 사용자: JWT 발급 및 홈으로 리디렉션
            Member member = memberRepository.findByEmail(email).get();

            // AccessToken 및 RefreshToken 생성
            String accessToken = jwtProvider.createAccessToken(member.getEmail(), member.getRole(), null).getToken();
            String refreshToken = jwtProvider.createRefreshToken(member.getEmail(), member.getRole(), null).getToken();

            // RefreshToken 쿠키에 저장
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // HTTPS 사용 시
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int) (jwtProvider.getRefreshExpires() / 1000)); // 밀리초를 초로 변환
            response.addCookie(refreshCookie);

            // AccessToken 쿠키에 저장
            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(false); // HTTPS 사용 시
            accessCookie.setPath("/");
            accessCookie.setMaxAge((int) (jwtProvider.getAccessExpires() / 1000)); // 밀리초를 초로 변환
            response.addCookie(accessCookie);

            // 홈 페이지로 리디렉션
            response.sendRedirect("/");
        } else {
            // DB에 존재하지 않는 사용자: 추가 정보 입력 페이지로 리디렉션
            response.sendRedirect("/signup/confirm");
        }
    }
}
