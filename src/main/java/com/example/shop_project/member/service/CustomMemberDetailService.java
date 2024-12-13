package com.example.shop_project.member.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomMemberDetailService implements UserDetailsService {
	private final MemberRepository memberRepository;
	
	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> 
        	new UsernameNotFoundException("등록되지 않은 회원입니다: " + email));
        
        if (member.getWithdraw() == true) {
            // 탈퇴한 사용자이므로 예외 발생 (로그인 불가)
            throw new UsernameNotFoundException("탈퇴한 회원입니다. 다시 가입하세요.");
        }
        
        return new User(member.getEmail(), member.getPassword(), 
                        Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
    }
}
