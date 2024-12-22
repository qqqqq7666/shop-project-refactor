package com.example.shop_project.member.controller;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.MemberGlobalExceptionHandler;
import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.member.service.MemberService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // GET 요청
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // POST 요청
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // 상태 코드 검증
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;   // 뷰 이름 검증

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

public class MemberControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@InjectMocks
    private MemberController memberController;
	
    private MemberService memberService = mock(MemberService.class);
    private MemberRepository memberRepository = mock(MemberRepository.class);
	private Member mockMember = mock(Member.class);
	private MemberRequestDTO mockMemberRequestDTO = mock(MemberRequestDTO.class);
	
	@BeforeEach
    void 테스트전처리() {
		MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
        		.setControllerAdvice(new MemberGlobalExceptionHandler())
        		.build();
    }

	
	@Test	// 페이지 요청이 정상적으로 200 OK를 반환하고, join 뷰를 렌더링하는지 확인.
	void 회원가입_페이지접속() throws Exception {
	    mockMvc.perform(get("/join"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("member/join"));
	}
	
	@Test
	void 회원가입_성공() throws Exception {
		// given
		 mockMvc.perform(post("/join")
				.param("email", "test432@example.com")
                .param("password", "Qlalfqjsgh123!")
                .param("confirmPassword", "Qlalfqjsgh123!")
                .param("name", "김똘똘")
                .param("nickname", "테스트닉네임")
                .param("phone", "010-1234-5678")
                .param("postNo", "12345")
                .param("address", "시흥시 정왕동")
                .param("addressDetail", "2309-5"))
		 // when
				 .andExpect(status().is3xxRedirection()) // 리다이렉트
		         .andExpect(redirectedUrl("/")); // 메인화면으로
		// Then: 서비스가 호출되었는지 확인
	        verify(memberService, times(1)).Join(any(MemberRequestDTO.class));
	}
	
	@ParameterizedTest
    @CsvSource({
            " , Qlalfqjsgh123!, Qlalfqjsgh123!, 김상수, 테스트닉네임, 010-1234-5678, 12345, 제주도, 101호", // 이메일 비어 있음
            "email, Qlalfqjsgh123!, Qlalfqjsgh123!, 김상수, 테스트닉네임, 010-1234-5678, 12345, 제주도, 101호", // 이메일 형식 잘못됨
            "test@example.com, Qlalf, Qlalf, 김상수, 테스트닉네임, 010-1234-5678, 12345, 제주도, 101호", // 비밀번호 너무 짧음
            "test@example.com, Qlalfqjsgh123!, Qlalfqjsgh123!, , 테스트닉네임, 010-1234-5678, 12345, 제주도, 101호", // 이름 비어 있음
            "test@example.com, Qlalfqjsgh123!, Qlalfqjsgh123!, 김상수, , 010-1234-5678, 12345, 제주도, 101호", // 닉네임 비어 있음
            "test@example.com, Qlalfqjsgh123!, Qlalfqjsgh123!, 김상수, 테스트닉네임, 12345, 12345, 제주도, 101호", // 전화번호 형식 잘못됨
            "test@example.com, Qlalfqjsgh123!, Qlalfqjsgh123!, 김상수, 테스트닉네임, 010-1234-5678, , 제주도, 101호" // 주소 비어 있음
    })
    void 회원가입_유효성검사_실패(String email, String password, String confirmPassword, String name,
                             String nickname, String phone, String postNo, String address, String addressDetail) throws Exception {
		// given
		mockMvc.perform(post("/join")
                .param("email", email)
                .param("password", password)
                .param("confirmPassword", confirmPassword)
                .param("name", name)
                .param("nickname", nickname)
                .param("phone", phone)
                .param("postNo", postNo)
                .param("address", address)
                .param("addressDetail", addressDetail))
       // when
                .andExpect(status().isOk())
                .andExpect(view().name("member/join")) 
				.andExpect(model().attributeExists("error")); 
    }
	
	@Test
	void 로그인_페이지_접속() throws Exception {
	    // Then
	    mockMvc.perform(get("/login"))
	            .andExpect(status().isOk())
	            .andExpect(view().name("member/login")); 
	}
	
	@Test
	void 마이페이지_접속_인증되지않은사용자() throws Exception {

	    // Then
	    mockMvc.perform(get("/mypage"))
	            .andExpect(status().is3xxRedirection()) // 리다이렉트
	            .andExpect(redirectedUrl("/login")); 
	}
	
	@Test
	void 마이페이지_접속_인증된사용자() throws Exception {
		 // given
        Member mockMember = new Member();
        mockMember.setEmail("test@example.com");

        // When
        when(memberService.findByEmail("test@example.com")).thenReturn(mockMember);

        // Then
        mockMvc.perform(get("/mypage").principal(() -> "test@example.com"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("member/mypage")) 
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attribute("member", mockMember)); 
	}
	
	@Test
	void 회원정보수정_페이지_접속_인증되지않은사용자() throws Exception {
	    // Then
	    mockMvc.perform(get("/mypage/edit"))
	            .andExpect(status().is3xxRedirection())
	            .andExpect(redirectedUrl("/login"));
	}
	
	@Test
    void 회원정보수정_페이지_접속_인증된사용자() throws Exception {
		// given
	    Member mockMember = new Member();
	    mockMember.setEmail("test@example.com");

	    // when
	    when(memberService.findByEmail("test@example.com")).thenReturn(mockMember);

	    // Then
	    mockMvc.perform(get("/mypage/edit")
	            .principal(() -> "test@example.com")) 
	            .andExpect(status().isOk()) 
	            .andExpect(view().name("member/editProfile")) 
	            .andExpect(model().attributeExists("member")) 
	            .andExpect(model().attribute("member", mockMember)); 
    }
	
	@Test
	void 회원정보수정_성공() throws Exception {
	    // given
		// 기존정보
	    Member mockMember = new Member();
	    mockMember.setEmail("test@example.com");
	    mockMember.setNickname("기존 닉네임");

	    // 요청정보
	    MemberRequestDTO mockRequest = new MemberRequestDTO();
	    mockRequest.setNickname("새 닉네임");
	    mockRequest.setPhone("010-1234-5678");

	    // when
	    when(memberService.findByEmail("test@example.com")).thenReturn(mockMember);

	    // then
	    mockMvc.perform(post("/mypage/edit")
	            .principal(() -> "test@example.com") 
	            .param("nickname", mockRequest.getNickname())
	            .param("phone", mockRequest.getPhone()))
	            .andExpect(status().is3xxRedirection()) 
	            .andExpect(redirectedUrl("/mypage")); 

	    verify(memberService, times(1)).updateMember(mockMember, mockRequest);
	}
	

	@Test
	void 회원탈퇴_성공() throws Exception {
		// given
	    Member mockMember = new Member();
	    mockMember.setEmail("test@example.com");
	    mockMember.setWithdraw(false); // 초기는 탈퇴 x

	    // Mock된 Authentication 설정
	    Authentication mockAuthentication = mock(Authentication.class);
	    when(mockAuthentication.getName()).thenReturn("test@example.com");

	    // when
	    when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockMember));

	    // then
	    mockMvc.perform(post("/mypage/withdraw")
	            .principal(mockAuthentication)) // Mock된 Authentication 객체 전달
	            .andExpect(status().isOk()) 
	            .andExpect(content().contentType("text/plain;charset=UTF-8"))
	            .andExpect(content().string("회원 탈퇴가 완료되었습니다."));


	    assertTrue(mockMember.getWithdraw(), "회원 탈퇴 상태가 true로 변경되어야 합니다.");

	    verify(memberRepository, times(1)).save(mockMember);
	}	
}
