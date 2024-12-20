package com.example.shop_project.member.controller;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.shop_project.member.MemberGlobalExceptionHandler;
import com.example.shop_project.member.repository.MemberRepository;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MemberRestControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@InjectMocks
    private MemberRestController memberRestController;
	
	private MemberRepository memberRepository = mock(MemberRepository.class);
	
	@BeforeEach
    void 테스트전처리() {
		MockitoAnnotations.openMocks(this);
		
		mockMvc = MockMvcBuilders.standaloneSetup(memberRestController).build();
    }
	
	@Test
	void 이메일_중복존재() throws Exception {
	    // given
	    String existingEmail = "test@example.com";
	    
	    // when
	    when(memberRepository.existsByEmail(existingEmail)).thenReturn(true);

	    // then
	    mockMvc.perform(get("/api/member/check-email")
	            .param("email", existingEmail)) 
	            .andExpect(status().isConflict()) 
	            .andExpect(jsonPath("$.exists").value(true)); 
	}

	@Test
	void 이메일_중복존재하지않음() throws Exception {
	    // given
	    String nonExistingEmail = "test@example.com";
	    
	    // when
	    when(memberRepository.existsByEmail(nonExistingEmail)).thenReturn(false);

	    // then
	    mockMvc.perform(get("/api/member/check-email")
	            .param("email", nonExistingEmail)) 
	            .andExpect(status().isOk()) 
	            .andExpect(jsonPath("$.exists").value(false)); 
	}
	
	@Test
    void 닉네임_중복존재() throws Exception {
        // given
        String nickname = "중복닉네임";
        
        // when
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);

        // then
        mockMvc.perform(get("/api/member/check-nickname")
                .param("nickname", nickname))
                .andExpect(status().isConflict()) 
                .andExpect(jsonPath("$.exists").value(true)); 
    }

    @Test
    void 닉네임_중복존재안함() throws Exception {
        // given
        String nickname = "새닉네임";
        
        // when
        when(memberRepository.existsByNickname(nickname)).thenReturn(false);

        // then
        mockMvc.perform(get("/api/member/check-nickname")
                .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void 닉네임_현재닉네임과_동일() throws Exception {
        // given
        String nickname = "현재닉네임";
        String currentNickname = "현재닉네임";

        // then
        mockMvc.perform(get("/api/member/check-nickname")
                .param("nickname", nickname)
                .param("currentNickname", currentNickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void 닉네임다르고_중복존재() throws Exception {
        // given
        String nickname = "현재닉네임";
        String currentNickname = "다른닉네임";
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);

        // then
        mockMvc.perform(get("/api/member/check-nickname")
                .param("nickname", nickname)
                .param("currentNickname", currentNickname))
                .andExpect(status().isConflict()) 
                .andExpect(jsonPath("$.exists").value(true)); 
    }
}
