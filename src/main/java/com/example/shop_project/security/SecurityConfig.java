package com.example.shop_project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	//private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        		.sessionManagement(session -> session
        				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)		// 서버에서 세션 사용 x 인증 정보는 클라이언트에서 관리
        		)
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers("/admin/**").hasAuthority("ADMIN") // 관리자만 접근 허용
                        .requestMatchers("/**").permitAll()		// 후에 접근 허용 경로 수정 필요
                )
                //.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지
                        .loginProcessingUrl("/perform_login") // 폼 액션 URL
                        .defaultSuccessUrl("/mypage", true) // 임시로 로그인 성공 시 마이페이지로 이동
                        .failureUrl("/login?error=true") // 실패 시 이동 경로
                        .usernameParameter("email") // 이메일 필드
                        .passwordParameter("password") // 비밀번호 필드
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")               // 로그아웃 처리 URL
                        .logoutSuccessUrl("/login")         // 로그아웃 성공 후 이동할 URL
                        .invalidateHttpSession(true)        // 세션 무효화
                        .deleteCookies("JSESSIONID")        // 쿠키 삭제
                )
                .csrf(AbstractHttpConfigurer::disable);		// 후에 csrf

        return http.build();
    }
}
