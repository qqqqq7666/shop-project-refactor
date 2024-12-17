package com.example.shop_project.member.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder  passwordEncoder;
	
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
}
