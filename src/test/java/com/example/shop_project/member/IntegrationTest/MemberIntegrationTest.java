package com.example.shop_project.member.IntegrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberIntegrationTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MemberService memberService;
    
    @Test
    public void 회원가입_통합테스트() throws Exception {
    	// given
    	MemberRequestDTO newMember = new MemberRequestDTO();
    	newMember.setEmail("myIntergrationTest@example.com");
    	newMember.setPassword("Password123!");
    	newMember.setConfirmPassword("Password123!");
    	newMember.setNickname("aabbcc");
    	newMember.setName("김철수");
    	newMember.setPhone("010-1234-5678");
    	newMember.setPostNo("12345");
    	newMember.setAddress("서울");
    	newMember.setAddressDetail("광진구");
    	
    	// when
        mockMvc.perform(post("/join")
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", newMember.getEmail())
                .param("password", newMember.getPassword())
                .param("confirmPassword", newMember.getConfirmPassword())
                .param("nickname", newMember.getNickname())
                .param("name", newMember.getName())
                .param("phone", newMember.getPhone())
                .param("postNo", newMember.getPostNo())
                .param("address", newMember.getAddress())
                .param("addressDetail", newMember.getAddressDetail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
                
    	
        // then
    	Member savedMember = memberService.findByEmail("myIntergrationTest@example.com");
    }
    
    @Test
    @WithMockUser(username = "myIntegrationTest@example.com", roles = "USER")
    public void 회원정보수정_통합테스트() throws Exception {
        // given
        MemberRequestDTO signupRequest = new MemberRequestDTO();
        signupRequest.setEmail("myIntegrationTest@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");
        signupRequest.setNickname("aabbcc");
        signupRequest.setName("김철수");
        signupRequest.setPhone("010-1234-5678");
        signupRequest.setPostNo("12345");
        signupRequest.setAddress("서울");
        signupRequest.setAddressDetail("광진구");
        
        mockMvc.perform(post("/join")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", signupRequest.getEmail())
                .param("password", signupRequest.getPassword())
                .param("confirmPassword", signupRequest.getConfirmPassword())
                .param("nickname", signupRequest.getNickname())
                .param("name", signupRequest.getName())
                .param("phone", signupRequest.getPhone())
                .param("postNo", signupRequest.getPostNo())
                .param("address", signupRequest.getAddress())
                .param("addressDetail", signupRequest.getAddressDetail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        
        // when (회원정보수정에서 이름과 이메일은 수정x)
        MemberRequestDTO updateRequest = new MemberRequestDTO();
        updateRequest.setEmail(signupRequest.getEmail()); 
        updateRequest.setPassword("Newpassword123!");
        updateRequest.setConfirmPassword("Newpassword123!");
        updateRequest.setNickname("newnickname");
        updateRequest.setName(signupRequest.getName());
        updateRequest.setPhone("010-1212-1234");
        updateRequest.setPostNo("98765");
        updateRequest.setAddress("부산");
        updateRequest.setAddressDetail("해운대구");
        
        mockMvc.perform(post("/mypage/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", updateRequest.getPassword())
                .param("confirmPassword", updateRequest.getConfirmPassword())
                .param("nickname", updateRequest.getNickname())
                .param("phone", updateRequest.getPhone())
                .param("postNo", updateRequest.getPostNo())
                .param("address", updateRequest.getAddress())
                .param("addressDetail", updateRequest.getAddressDetail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));
        
        // then
        Member updatedMember = memberRepository.findByEmail("myIntegrationTest@example.com").orElse(null);
        assertNotNull(updatedMember, "저장된 회원이 존재해야 합니다.");
        assertEquals("newnickname", updatedMember.getNickname(), "닉네임이 업데이트되어야 합니다.");
        assertEquals("010-1212-1234", updatedMember.getPhone(), "전화번호가 업데이트되어야 합니다.");
        assertEquals("98765", updatedMember.getPostNo(), "우편번호가 업데이트되어야 합니다.");
        assertEquals("부산", updatedMember.getAddress(), "주소가 업데이트되어야 합니다.");
        assertEquals("해운대구", updatedMember.getAddressDetail(), "상세 주소가 업데이트되어야 합니다.");
    }
}
