package com.example.shop_project;

import org.springframework.stereotype.Service;
import com.example.shop_project.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;
	
	public void Join(Member member) {
		memberRepository.save(member);	
	}
}
