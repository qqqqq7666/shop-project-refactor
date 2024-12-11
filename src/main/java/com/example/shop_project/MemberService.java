package com.example.shop_project;

import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.shop_project.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;
	
	public void Join(Member member) {
		Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
        if (existingMember.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
		
		memberRepository.save(member);	
	}
}
