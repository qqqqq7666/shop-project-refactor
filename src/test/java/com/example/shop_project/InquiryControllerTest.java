package com.example.shop_project;

import com.example.shop_project.inquiry.controller.InquiryController;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InquiryControllerTest {

    @Mock
    private InquiryService inquiryService;

    @Mock
    private Model model;

    @InjectMocks
    private InquiryController inquiryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("문의 목록 조회")
    void showInquiriesByProduct() {
        // given
        Long productId = 1L;
        List<Inquiry> mockInquiries = new ArrayList<>();
        mockInquiries.add(new Inquiry());
        //when(inquiryService.getInquiriesByProductId(productId)).thenReturn(mockInquiries);

        // when
        String viewName = inquiryController.showInquiriesByProduct(productId, model);

        // then
        assertEquals("inquiry/list", viewName);
        verify(model, times(1)).addAttribute("inquiries", mockInquiries);
        verify(model, times(1)).addAttribute("productId", productId);
    }

    @Test
    @DisplayName("문의 작성 페이지 이동")
    void showCreatePage() {
        // given
        Long productId = 1L;

        // when
        String viewName = inquiryController.showCreatePage(productId, model);

        // then
        assertEquals("inquiry/create", viewName);
        verify(model, times(1)).addAttribute("productId", productId);
    }

    @Test
    @DisplayName("문의 생성 처리")
    void createInquiry() {
        // given
        Long productId = 1L;
        InquiryRequestDto dto = new InquiryRequestDto();
        String userEmail = "test@example.com";

        mockSecurityContext(userEmail);

        when(inquiryService.createInquiry(productId, dto, userEmail)).thenReturn(null);

        // when
        String viewName = inquiryController.createInquiry(productId, dto);

        // then
        assertEquals("redirect:/products/" + productId + "/inquiries", viewName);
        verify(inquiryService, times(1)).createInquiry(productId, dto, userEmail);
    }

    @Test
    @DisplayName("문의 조회")
    void getInquiryById() {
        // given
        Long productId = 1L;
        Long inquiryId = 10L;

        Member mockMember = new Member();
        mockMember.setNickname("testUser");

        Inquiry mockInquiry = new Inquiry();
        mockInquiry.setId(inquiryId);
        mockInquiry.setTitle("Test Title");
        mockInquiry.setContent("Test Content");
        mockInquiry.setMember(mockMember); // 작성자 정보 추가

        // Mock 서비스 메서드
        when(inquiryService.getInquiryByProductIdAndInquiryId(productId, inquiryId)).thenReturn(mockInquiry);

        // when
        String viewName = inquiryController.getInquiryById(productId, inquiryId, model);

        // then
        assertEquals("inquiry/detail", viewName);
        verify(model, times(1)).addAttribute("inquiry", mockInquiry);
        verify(model, times(1)).addAttribute("productId", productId);
    }

    @Test
    @DisplayName("문의 삭제")
    void deleteInquiry() {
        // given
        Long productId = 1L;
        Long inquiryId = 10L;

        // when
        ResponseEntity<Void> response = inquiryController.deleteInquiry(productId, inquiryId);

        // then
        assertEquals(204, response.getStatusCodeValue());
        verify(inquiryService, times(1)).deleteInquiry(inquiryId);
    }

    // SecurityContextHolder Mock 설정
    private void mockSecurityContext(String userEmail) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(userEmail);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}