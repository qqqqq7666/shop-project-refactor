package com.example.shop_project.oauth2;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.shop_project.jwt.dto.JwtTokenDto;
import com.example.shop_project.member.service.MemberService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final MemberService memberService;

    public CustomOAuth2UserService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        GoogleUserInfoDto googleUserInfo = GoogleUserInfoDto.builder()
                .email(email)
                .name(name)
                .providerId(providerId)
                .build();

        // 사용자를 저장하거나 업데이트
        JwtTokenDto jwtTokenDto = memberService.googleLogin(googleUserInfo);
        
        // OAuth2User를 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("accessToken", jwtTokenDto.getAccessToken()),
                "email"
        );
    }
}

