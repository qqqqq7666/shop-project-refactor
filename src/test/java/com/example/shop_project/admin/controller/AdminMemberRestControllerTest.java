package com.example.shop_project.admin.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminMemberRestControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    @BeforeEach
    void 테스트전_기본회원데이터() {
        testMember = Member.builder()
                .email("user@example.com")
                .password("password")
                .name("Test User")
                .nickname("tester")
                .phone("010-1234-5678")
                .postNo("12345")
                .address("서울시 중구")
                .addressDetail("상세주소")
                .role(Role.USER)
                .withdraw(false)
                .build();

        memberRepository.save(testMember);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void 권한_변경_API_성공() throws Exception {
        // 권한 변경 요청
        String requestBody = "{ \"role\": \"ADMIN\" }";

        mockMvc.perform(put("/api/admin/members/role/" + testMember.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk()) // 200 응답
                .andExpect(content().string("권한이 변경되었습니다."));

        // 데이터베이스에서 권한 확인
        Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assert updatedMember.getRole() == Role.ADMIN; // 권한이 ADMIN으로 변경되었는지 확인
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void 회원_탈퇴_API_성공() throws Exception {
        mockMvc.perform(put("/api/admin/members/withdraw/" + testMember.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 응답
                .andExpect(content().string("회원이 탈퇴 처리되었습니다."));

        // 데이터베이스에서 탈퇴 상태 확인
        Member updatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assert updatedMember.getWithdraw(); // 탈퇴 상태 확인
    }
}
