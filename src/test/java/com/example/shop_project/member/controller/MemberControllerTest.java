package com.example.shop_project.member.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test	// 페이지 요청이 정상적으로 200 OK를 반환하고, join 뷰를 렌더링하는지 확인.
	void 회원가입_페이지접속() throws Exception {
	    mockMvc.perform(get("/join"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("member/join"));
	}
	
	@Test	// 모든 필드를 올바르게 입력하면 성공적으로 /login으로 리다이렉트.
	void 회원가입_모든필드_입력확인() throws Exception {
		String base64Encoded = Base64.getEncoder().encodeToString("ValidPass1!".getBytes(StandardCharsets.UTF_8));

	    mockMvc.perform(post("/join")
	            .param("email", "test@example.com")
	            .param("nickname", "tester")
	            .param("name", "testname")
	            .param("password", base64Encoded) // 실제 특수문자 인코딩 필요할 수도 있음
	            .param("confirmPassword", base64Encoded)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andExpect(status().is3xxRedirection())
	    .andExpect(redirectedUrl("member/login"));
	}
	
	// 잘못된 이메일 주소를 보내면 join 페이지로 돌아가고 error 메시지를 포함하는지 확인.
	@ParameterizedTest	
	@ValueSource(strings = {"email", "@example.com", "email@.com", "email@example.c", 
            "#email@example.com", "#@!@example.com", "email@example!@!.com"})			// 실패 케이스 목록
	void 회원가입_이메일_유효성검사(String invalidEmail) throws Exception {
		String base64Encoded = Base64.getEncoder().encodeToString("ValidPass1!".getBytes(StandardCharsets.UTF_8));
		
	    mockMvc.perform(post("/join")
	            .param("email", invalidEmail)				// 실패 케이스 
	            .param("nickname", "tester")
	            .param("name", "testname")
	            .param("password", base64Encoded)
	            .param("confirmPassword", base64Encoded)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andExpect(status().isOk())
	    .andExpect(view().name("member/join"))
	    .andExpect(model().attribute("error", "올바른 이메일 형식이 아닙니다."));
	}
	
	// 비밀번호 유효성 검사
	@ParameterizedTest
	@ValueSource(strings = {
	        "short",            // 너무 짧은 비밀번호
	        "NoNumber!",        // 숫자가 없는 비밀번호
	        "nonumber!",        // 숫자와 대문자가 없는 비밀번호
	        "NOLOWERCASE1!",    // 소문자가 없는 비밀번호
	        "NoSpecial1",       // 특수문자가 없는 비밀번호
	        " ",                // 공백만 있는 비밀번호
	})
	void 회원가입_비밀번호_유효성검사(String invalidPassword) throws Exception {
		String base64EncodedPassword = Base64.getEncoder().encodeToString(invalidPassword.getBytes(StandardCharsets.UTF_8));
	    String base64EncodedConfirmPassword = base64EncodedPassword;

	    mockMvc.perform(post("/join")
	            .param("email", "test@example.com")      // 정상적인 이메일
	            .param("nickname", "tester")
	            .param("name", "testname")
	            .param("password", base64EncodedPassword)          // 실패 케이스 비밀번호
	            .param("confirmPassword", base64EncodedConfirmPassword)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andDo(print())  // 요청 및 응답 로그 출력
	    .andExpect(status().isOk())  // 다시 회원가입 페이지로 이동
	    .andExpect(view().name("member/join"))
	    .andExpect(model().attribute("error", "비밀번호는 최소 8글자이며 대소문자, 숫자, 특수문자를 최소 하나씩 포함해야 합니다."));
	}
	
	// 비밀번호와 비밀번호 확인 불일치 확인
	@Test
	void 회원가입_비밀번호_일치확인() throws Exception {
	    // Base64로 인코딩된 비밀번호와 비밀번호 확인 (다르게 설정)
	    String base64Password = Base64.getEncoder().encodeToString("ValidPass1!".getBytes(StandardCharsets.UTF_8));
	    String base64ConfirmPassword = Base64.getEncoder().encodeToString("InvalidPass1!".getBytes(StandardCharsets.UTF_8));

	    mockMvc.perform(post("/join")
	            .param("email", "test@example.com")   // 정상적인 이메일
	            .param("nickname", "tester")
	            .param("name", "testname")
	            .param("password", base64Password)             // 비밀번호
	            .param("confirmPassword", base64ConfirmPassword) // 비밀번호 확인 (불일치)
	            .param("phone", "010-1234-5678")
	            .param("postNo", "12345")
	            .param("address", "서울시 중구")
	            .param("addressDetail", "상세주소")
	    )
	    .andDo(print())  // 요청 및 응답 로그 출력
	    .andExpect(status().isOk())  // 에러가 나면 다시 join 페이지로 이동
	    .andExpect(view().name("member/join"))  // 뷰 이름이 member/join인지 확인
	    .andExpect(model().attribute("error", "비밀번호가 일치하지 않습니다."));  // 에러 메시지 확인
	}
	
	@Test	 // 로그인 페이지 요청이 200 OK와 login 뷰를 반환하는지 확인.
	void 로그인_페이지접속() throws Exception {
	    mockMvc.perform(get("/login"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("member/login"));
	}
	
	// db에 존재하지 않는 이메일로 로그인 시도시 실패
	@Test
	void 로그인_존재하지않는_이메일() throws Exception {
		
	}
	
	// 존재하는 이메일을 입력했지만 비밀번호가 틀리면 로그인 실패
	@Test
	void 로그인_비밀번호불일치() throws Exception {
		
	}
	
	@Test	// 인증된 사용자(로그인된)는 mypage접근시 200 확인
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	void 마이페이지_인증된사용자_접속() throws Exception {
	    mockMvc.perform(get("/mypage"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("member/mypage"));
	}

	@Test	// 인증되지 않은 사용자(비로그인)는 login으로 리다이렉트
	void 마이페이지_인증되지않은사용자_접속() throws Exception {
	    mockMvc.perform(get("/mypage")
	            .with(anonymous())) // 인증되지 않은 사용자 모의
	            .andExpect(status().is3xxRedirection()) // 3xx 리다이렉션 상태 코드 확인
	            .andExpect(redirectedUrlPattern("/login*")); // 로그인 페이지로 리다이렉션 확인
	}
	
	@Test	// 인증된 상태(로그인)에서 POST /mypage/withdraw를 호출하면 "회원 탈퇴가 완료되었습니다."라는 메시지를 반환
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	void 마이페이지_삭제_메시지반환() throws Exception {
	    mockMvc.perform(post("/mypage/withdraw"))
	            .andExpect(status().isOk())
	            .andExpect(content().string(containsString("회원 탈퇴가 완료되었습니다.")));
	}
	
}
