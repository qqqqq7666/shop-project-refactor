package com.example.shop_project.member.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
		boolean isExists = memberRepository.existsByEmail(email);
        Map<String, Boolean> response = Collections.singletonMap("exists", isExists);

        // 이메일 중복 시 409 Conflict 반환, 그렇지 않으면 200 OK
        if (isExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
	}
	
	@GetMapping("/check-nickname")
	public ResponseEntity<Map<String, Boolean>> checkNickname(
            @RequestParam("nickname") String nickname,
            @RequestParam(value = "currentNickname", required = false) String currentNickname) {
        
        // 현재 닉네임과 동일하면 중복으로 간주하지 않음
        boolean isExists = (currentNickname == null || !nickname.equals(currentNickname)) 
                			&& memberRepository.existsByNickname(nickname);
        Map<String, Boolean> response = Collections.singletonMap("exists", isExists);

        // 닉네임 중복 시 409 Conflict 반환, 그렇지 않으면 200 OK
        if (isExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }
}
