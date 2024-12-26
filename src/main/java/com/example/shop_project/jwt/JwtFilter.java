package com.example.shop_project.jwt;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;
import org.springframework.util.StringUtils;
import com.example.shop_project.jwt.JwtProviderImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.shop_project.jwt.AuthTokenImpl;
import static com.example.shop_project.jwt.MemberConstants.AUTHORIZATION_TOKEN_KEY;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl tokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 1) 쿠키에서 Access Token 추출
        Optional<String> accessToken = getAccessTokenFromCookie(request);

        // 2) 토큰 검증 및 SecurityContext 설정
        if (accessToken.isPresent()) {
            AuthTokenImpl jwtToken = tokenProvider.convertAuthToken(accessToken.get());

            if (jwtToken.validate()) {
                try {
                    Authentication authentication = tokenProvider.getAuthentication(jwtToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    // 인증 중 발생한 예외 처리
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                // 토큰이 유효하지 않을 경우
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
    
    private Optional<String> getAccessTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
}
