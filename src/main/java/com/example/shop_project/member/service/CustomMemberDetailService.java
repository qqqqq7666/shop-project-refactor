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
        
        return new User(member.getEmail(), member.getPassword(), 
                        Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
    }
}
