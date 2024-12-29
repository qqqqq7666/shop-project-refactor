package com.example.shop_project.admin.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

public class AdminMemberViewControllerTest {
	@InjectMocks
    private AdminMemberViewController adminMemberController;

    @Mock
    private MemberRepository memberRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void 테스트전처리() {
    	MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminMemberController).build();
    }
    
    @Test
    void 회원관리_페이지_데이터_확인() throws Exception {
        // given
        long userCount = 100; // USER 권한 사용자 수
        long adminCount = 10; // ADMIN 권한 사용자 수

        List<Member> mockMembers = Arrays.asList(
            Member.builder()
                  .id(1L)
                  .email("user@example.com")
                  .nickname("유저")
                  .role(Role.USER)
                  .build(),
            Member.builder()
                  .id(2L)
                  .email("admin@example.com")
                  .nickname("관리자")
                  .role(Role.ADMIN)
                  .build()
        );

        // when
        when(memberRepository.countByRole(Role.USER)).thenReturn(userCount);
        when(memberRepository.countByRole(Role.ADMIN)).thenReturn(adminCount);
        when(memberRepository.findAll()).thenReturn(mockMembers);

        // then
        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("admin/members")) 
                .andExpect(model().attribute("userCount", userCount)) 
                .andExpect(model().attribute("adminCount", adminCount)) 
                .andExpect(model().attribute("members", mockMembers)); 
    }
}
