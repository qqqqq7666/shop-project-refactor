package com.example.shop_project.member.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@Mock
    MemberRepository memberRepository;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	MemberService memberService;
	
	@Test
	void 회원가입_성공_확인() {
		// given (준비)
		MemberRequestDTO requestDTO = new MemberRequestDTO();
		requestDTO.setEmail("test@example.com");
        requestDTO.setNickname("tester");
        requestDTO.setPassword("ValidPass1!");
        requestDTO.setConfirmPassword("ValidPass1!");
        requestDTO.setPhone("010-1234-5678");
        requestDTO.setPostNo("12345");
        requestDTO.setAddress("서울시 중구");
        requestDTO.setAddressDetail("상세주소");
        
        when(passwordEncoder.encode("ValidPass1!")).thenReturn("encodedPassword");
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("tester")).thenReturn(Optional.empty());
        
        // when (실제 동작)
        memberService.Join(requestDTO);
        
		// then (검증)
        // memberRepository의 save() 메서드가 한 번 호출되었는지 확인
        verify(memberRepository, times(1)).save(any(Member.class));
        
	}
	
	// 이메일 중복 확인
	@Test
    void 이메일_중복_확인() {
        // given
        MemberRequestDTO requestDTO = new MemberRequestDTO();
        requestDTO.setEmail("exists@example.com");
        requestDTO.setNickname("tester");
        requestDTO.setPassword("ValidPass1!");
        requestDTO.setConfirmPassword("ValidPass1!");

        Member existingMember = new Member();
        existingMember.setEmail("exists@example.com");
        when(memberRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(existingMember));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> memberService.Join(requestDTO));
    }
	
	// 닉네임 중복 확인
	@Test
    void 닉네임_중복_확인() {
        // given


        // when & then

    }
	
}
