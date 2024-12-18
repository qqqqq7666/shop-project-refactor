package com.example.shop_project.jwt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// JWT 필터 추가
// 클라이언트에서 전달된 JWT를 해석하여 사용자를 인증하는 필터
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);
		
		// validateToken 으로 서명을 확인하고 유효기간이 지나지 않았는지 검증
		if (token != null && jwtTokenProvider.validateToken(token)) {
			// jwt토큰에서 claims(페이로드 부분)을 추출
			Claims claims = jwtTokenProvider.getClaimsFromToken(token);
			
			String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            // 스트림을 이용해 각 역할 문자열을 SimpleGrantedAuthority로 매핑하고 이를 리스트로 수집
            // SimpleGrantedAuthority는 스프링시큐리티에서 권한을 표현하는 클래스 
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 스프링시큐리티에서 인증 정보를 표현하는 객체 생성 
            // 매개변수 : 사용자 이름, 비밀번호(jwt를 사용하므로 필요없음), 권한 목록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // 생성된 인증 정보를 스프링시큐리티의 SecurityContextHolder에 저장함
            // 이렇게 하면 애플리케이션의 다른 부분에서 인증된 사용자 정보 사용 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
		}
    }
	
	// Authorization 헤더에서 bearerToken토큰 추출
	private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
