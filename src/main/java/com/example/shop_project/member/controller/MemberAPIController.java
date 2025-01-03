package com.example.shop_project.member.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.jwt.dto.JwtTokenDto;
import com.example.shop_project.jwt.dto.JwtTokenResponse;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.oauth2.GoogleUserInfoDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberAPIController {
	private final MemberRepository memberRepository;
	private final MemberService memberService;
	private final PasswordEncoder  passwordEncoder;
	
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
	
	@PostMapping("/verify-password")
	@ResponseBody
	public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request, Principal principal) {
	    String inputPassword = request.get("password");
	    String userEmail = principal.getName(); // 현재 로그인한 사용자의 이메일

	    Member member = memberService.findByEmail(userEmail);

	    if (passwordEncoder.matches(inputPassword, member.getPassword())) {
	        return ResponseEntity.ok().build(); // 비밀번호 일치
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 비밀번호 불일치
	    }
	}
	
}
