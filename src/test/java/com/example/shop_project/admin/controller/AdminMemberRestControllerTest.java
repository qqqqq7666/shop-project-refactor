package com.example.shop_project.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.product.controller.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

public class AdminMemberRestControllerTest {
	@Autowired
    private MockMvc mockMvc;

    private MemberRepository memberRepository = mock(MemberRepository.class);

    @InjectMocks
    private AdminMemberRestController adminMemberRestController;

    @BeforeEach
    void 테스트전처리() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminMemberRestController)
        		.setControllerAdvice(new GlobalExceptionHandler())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
        		.build();
    }

    @Test
    void 권한변경_성공() throws Exception {
    	// given
        Long memberId = 1L;
        Member mockMember = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .role(Role.USER) // 바꾸기 전엔 유저권한
                .build();

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        // then
        mockMvc.perform(put("/api/admin/members/role/{id}", memberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) 
                .andExpect(content().string("권한이 변경되었습니다."));

        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).save(mockMember);
    }

    @Test
    void 회원삭제_성공() throws Exception {
        // given
        Long memberId = 1L;
        Member mockMember = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .withdraw(false) // 탈퇴되지 않은 상태
                .build();

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // then
        mockMvc.perform(put("/api/admin/members/withdraw/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("회원이 탈퇴 처리되었습니다."));

        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, times(1)).save(mockMember);
    }

    @Test
    void 회원삭제_실패_이미탈퇴됨() throws Exception {
        // given
        Long memberId = 1L;
        Member mockMember = Member.builder()
                .id(memberId)
                .email("test@example.com")
                .withdraw(true) // 이미 탈퇴된 상태
                .build();

        // when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // then
        mockMvc.perform(put("/api/admin/members/withdraw/{id}", memberId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("이미 탈퇴된 회원입니다."));

        verify(memberRepository, times(1)).findById(memberId);
        verify(memberRepository, never()).save(any(Member.class));
    }
}
