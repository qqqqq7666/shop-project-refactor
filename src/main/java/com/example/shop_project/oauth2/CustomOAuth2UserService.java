package com.example.shop_project.oauth2;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.Provider;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) 
           throws OAuth2AuthenticationException {
        // 1) 구글에서 사용자 정보 가져오기
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 2) 필요한 필드 파싱
        String email = oAuth2User.<String>getAttribute("email");
        String name  = oAuth2User.<String>getAttribute("name");
        String sub   = oAuth2User.<String>getAttribute("sub"); // 구글 고유ID
        
        // 3) 여기서는 DB insert 안 함!!!
        //    그냥 SecurityContext에 인증 정보만 채움

        // 4) 반환할 OAuth2User의 attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 5) 권한을 ROLE_USER 로 가정
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "email" // key: "email" or "sub"
        );
    }
}
