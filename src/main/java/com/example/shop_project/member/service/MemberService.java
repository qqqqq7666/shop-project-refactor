package com.example.shop_project.member.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shop_project.jwt.AuthTokenImpl;
import com.example.shop_project.jwt.JwtProviderImpl;
import com.example.shop_project.jwt.dto.JwtTokenDto;
import com.example.shop_project.jwt.dto.JwtTokenLoginRequest;
import com.example.shop_project.member.Provider;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.oauth2.GoogleUserInfoDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder  passwordEncoder;
	
	// jwt
	private final JwtProviderImpl jwtProvider;
	
	public void Join(MemberRequestDTO memberDTO) {
		// 이메일과 닉네임 중복 검사 (비동기로 중복 검사 하지만 안정성을 위해 추가)
		checkEmailAndNicknameExist(memberDTO);
	    
	    String encryptedPassword = passwordEncoder.encode(memberDTO.getPassword());
	    
	    Member member = Member.builder()
	            .email(memberDTO.getEmail())
	            .nickname(memberDTO.getNickname())
	            .password(encryptedPassword)
	            .name(memberDTO.getName())
	            .phone(memberDTO.getPhone())
	            .postNo(memberDTO.getPostNo())
	            .address(memberDTO.getAddress())
	            .addressDetail(memberDTO.getAddressDetail())
	            .build();
        
        memberRepository.save(member);	
	}
	
	public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
	
	public void updateMember(Member member, MemberRequestDTO memberRequestDTO) {
		String encryptedPassword = passwordEncoder.encode(memberRequestDTO.getPassword());

        // 요청 받은 정보로 기존의 회원 정보를 업데이트
        member.setNickname(memberRequestDTO.getNickname());
        member.setPhone(memberRequestDTO.getPhone());
        member.setPassword(encryptedPassword);
        member.setPostNo(memberRequestDTO.getPostNo());
        member.setAddress(memberRequestDTO.getAddress());
        member.setAddressDetail(memberRequestDTO.getAddressDetail());

        memberRepository.save(member);
    }
	
	private void checkEmailAndNicknameExist(MemberRequestDTO memberDTO) {
        Optional<Member> existingMemberByEmail = memberRepository.findByEmail(memberDTO.getEmail());
        if (existingMemberByEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Optional<Member> existingMemberByNickname = memberRepository.findByNickname(memberDTO.getNickname());
        if (existingMemberByNickname.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }
	
	// jwt
	public JwtTokenDto login(JwtTokenLoginRequest request) {
	    // 1) 이메일로 사용자 찾기
	    Member member = memberRepository.findByEmail(request.getEmail())
	        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

	    // 2) 이메일을 subject로 사용
	    String subject = member.getEmail();   // 예: "user@example.com"

	    // 3) Access Token 생성
	    AuthTokenImpl accessToken = jwtProvider.createAccessToken(
	        subject,          // <-- 'userId' 자리에 이메일을 보냄
	        member.getRole(),
	        null
	    );

	    // 4) Refresh Token 생성
	    AuthTokenImpl refreshToken = jwtProvider.createRefreshToken(
	        subject,          // <-- 마찬가지로 이메일
	        member.getRole(),
	        null
	    );

	    // 5) Dto로 묶어서 반환
	    return JwtTokenDto.builder()
	        .accessToken(accessToken.getToken())
	        .refreshToken(refreshToken.getToken())
	        .build();
	}
	
	// Google OAUTH2
	public Member saveOrUpdateGoogleUser(String email, String name, String providerId) {
        // 1) DB에서 이메일로 사용자 찾기
        return memberRepository.findByEmail(email)
            .map(user -> {
                // 2) 이미 존재하면, 필요한 경우 업데이트
                user.setName(name);           // 이름 업데이트 예시
                user.setProvider(Provider.GOOGLE);   // provider: GOOGLE

                return memberRepository.save(user);
            })
            .orElseGet(() -> {
                // 3) 없으면 새로 생성
                return memberRepository.save(Member.builder()
                    .email(email)
                    .name(name)
                    .provider(Provider.GOOGLE)

                    .role(Role.USER) // 일반 유저 권한
                    .build());
            });
    }
}
