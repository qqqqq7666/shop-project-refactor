package com.example.shop_project.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // GET 요청
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // POST 요청
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // 상태 코드 검증
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;   // 뷰 이름 검증

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test	// 페이지 요청이 정상적으로 200 OK를 반환하고, join 뷰를 렌더링하는지 확인.
	void testJoinPage() throws Exception {
	    mockMvc.perform(get("/join"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("join"));
	}
	
	@Test	// 모든 필드를 올바르게 입력하면 성공적으로 /login으로 리다이렉트.
	void testJoinSuccess() throws Exception {
		String base64Encoded = Base64.getEncoder().encodeToString("ValidPass1!".getBytes(StandardCharsets.UTF_8));

	    mockMvc.perform(post("/join")
	            .param("email", "test@example.com")
	            .param("nickname", "tester")
	            .param("password", base64Encoded) // 실제 특수문자 인코딩 필요할 수도 있음
	            .param("confirmPassword", base64Encoded)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andExpect(status().is3xxRedirection())
	    .andExpect(redirectedUrl("/login"));
	}
	
	@Test	// 잘못된 이메일 주소를 보내면 join 페이지로 돌아가고 error 메시지를 포함하는지 확인.
	void testJoinInvalidEmail() throws Exception {
		String base64Encoded = Base64.getEncoder().encodeToString("ValidPass1!".getBytes(StandardCharsets.UTF_8));
		
	    mockMvc.perform(post("/join")
	            .param("email", "email")
	            .param("nickname", "tester")
	            .param("password", base64Encoded)
	            .param("confirmPassword", base64Encoded)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andExpect(status().isOk())
	    .andExpect(view().name("join"))
	    .andExpect(model().attribute("error", "올바른 이메일 형식이 아닙니다."));
	}
	
	@Test	 // 로그인 페이지 요청이 200 OK와 login 뷰를 반환하는지 확인.
	void testLoginPage() throws Exception {
	    mockMvc.perform(get("/login"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("login"));
	}
	
	@Test	// 인증된 사용자(로그인된)는 mypage접근시 200 확인
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	void testMyPageAuthenticated() throws Exception {
	    mockMvc.perform(get("/mypage"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("mypage"));
	}

	@Test	// 인증되지 않은 사용자(비로그인)는 login으로 리다이렉트
	void testMyPageUnauthenticated() throws Exception {
	    mockMvc.perform(get("/mypage")
	            .with(anonymous())) // 인증되지 않은 사용자 모의
	            .andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 상태 코드 확인
	            .andExpect(redirectedUrlPattern("/login*")); // 로그인 페이지로 리다이렉션 확인
	}
	
	@Test	// 인증된 상태(로그인)에서 POST /mypage/withdraw를 호출하면 "회원 탈퇴가 완료되었습니다."라는 메시지를 반환
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	void testWithdraw() throws Exception {
	    mockMvc.perform(post("/mypage/withdraw"))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("회원 탈퇴가 완료되었습니다.")));
	}
	
}
