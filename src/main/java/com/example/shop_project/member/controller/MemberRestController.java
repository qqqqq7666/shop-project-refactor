package com.example.shop_project.member.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberRestController {
	private final MemberRepository memberRepository;
	
	@GetMapping("/check-email")
	public Map<String, Boolean> checkEmail(@RequestParam("email") String email) {
		boolean isExists = memberRepository.existsByEmail(email);
		
		return Collections.singletonMap("exists", isExists);
	}
	
	@GetMapping("/check-nickname")
    public Map<String, Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isExists = memberRepository.existsByNickname(nickname);
        return Collections.singletonMap("exists", isExists);
    }
}
