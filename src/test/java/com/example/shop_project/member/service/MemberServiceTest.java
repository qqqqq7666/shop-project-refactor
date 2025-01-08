package com.example.shop_project.member.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.shop_project.jwt.JwtProviderImpl;
import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProviderImpl jwtProvider;

    @InjectMocks
    private MemberService memberService;
    
    @Test
    void 회원가입_성공() {
        // given
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO();
        memberRequestDTO.setEmail("test@example.com");
        memberRequestDTO.setPassword("Password123!");
        memberRequestDTO.setNickname("temp닉네임");
        memberRequestDTO.setName("김이박");
        given(passwordEncoder.encode("Password123!")).willReturn("encodedPassword");

        Member savedMember = Member.builder()
            .email(memberRequestDTO.getEmail())
            .password("encodedPassword")
            .nickname(memberRequestDTO.getNickname())
            .name(memberRequestDTO.getName())
            .build();
        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        memberService.Join(memberRequestDTO);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }
    
    @Test
    void 중복_이메일_회원가입_실패() {
        // given
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO();
        memberRequestDTO.setEmail("unvalidEmail@example.com");

        given(memberRepository.findByEmail(memberRequestDTO.getEmail()))
            .willReturn(Optional.of(new Member()));

        // then
        assertThatThrownBy(() -> memberService.Join(memberRequestDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 이메일입니다.");
    }
    
    @Test
    void 중복_닉네임_회원가입_실패() {
        // given
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO();
        memberRequestDTO.setNickname("unvalidNickname");

        given(memberRepository.findByNickname(memberRequestDTO.getNickname()))
            .willReturn(Optional.of(new Member()));

        // then
        assertThatThrownBy(() -> memberService.Join(memberRequestDTO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 닉네임입니다.");
    }
    
//    @Test
//    void 이메일로_회원_조회_성공() {
//        // given
//        String email = "test@example.com";
//        Member member = Member.builder().email(email).build();
//
//        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
//
//        // when
//        Member foundMember = memberService.findByEmail(email);
//
//        // then
//        assertThat(foundMember.getEmail()).isEqualTo(email);
//    }
}
