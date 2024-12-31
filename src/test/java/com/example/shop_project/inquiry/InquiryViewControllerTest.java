package com.example.shop_project.inquiry;

import com.example.shop_project.inquiry.controller.InquiryViewController;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.InquiryType;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InquiryViewControllerTest {

    @Mock
    private InquiryService inquiryService;

    @Mock
    private Model model;

    @InjectMocks
    private InquiryViewController inquiryViewController;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("문의 목록 페이지 렌더링 성공")
    void testShowInquiriesByProduct() {
        // given
        Long productId = 1L;
        String currentUserEmail = "test@example.com";

        Product product = new Product();
        product.setProductId(productId);

        Member member = new Member();
        member.setEmail(currentUserEmail);
        member.setNickname("TestUser");

        Inquiry inquiry = Inquiry.builder()
                .id(1L)
                .product(product)
                .member(member)
                .title("Inquiry Title")
                .content("Inquiry Content")
                .date(LocalDate.now())
                .type(InquiryType.DETAILS)
                .isSecret(false)
                .build();

        List<Inquiry> inquiries = List.of(inquiry);
        when(inquiryService.getInquiriesByProductId(productId, currentUserEmail)).thenReturn(inquiries);

        // when
        String viewName = inquiryViewController.showInquiriesByProduct(productId, model);

        // then
        assertThat(viewName).isEqualTo("inquiry/list");
        verify(model).addAttribute(eq("inquiries"), anyList());
        verify(model).addAttribute("productId", productId);
    }

    @Test
    @DisplayName("문의 작성 페이지 렌더링 성공")
    void testShowCreatePage() {
        // given
        Long productId = 1L;

        // when
        String viewName = inquiryViewController.showCreatePage(productId, model);

        // then
        assertThat(viewName).isEqualTo("inquiry/create");
        verify(model).addAttribute("productId", productId);
    }

    @Test
    @DisplayName("문의 생성 처리 성공")
    void testCreateInquiry() {
        // given
        Long productId = 1L;
        InquiryRequestDto dto = new InquiryRequestDto();
        String userEmail = "test@example.com";

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        Authentication mockAuthentication = mock(Authentication.class);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
            when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
            when(mockAuthentication.getName()).thenReturn(userEmail);

            // when
            String redirectUrl = inquiryViewController.createInquiry(productId, dto);

            // then
            assertThat(redirectUrl).isEqualTo("redirect:/products/" + productId + "/inquiries");
            verify(inquiryService).createInquiry(productId, dto, userEmail);
        }
    }

    @Test
    @DisplayName("특정 문의 조회 성공")
    void testGetInquiryById() {
        // given
        Long productId = 1L;
        Long inquiryId = 1L;
        String currentUserEmail = "test@example.com";

        Product product = new Product();
        product.setProductId(productId);

        Member member = new Member();
        member.setEmail(currentUserEmail);
        member.setNickname("TestUser");

        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .product(product)
                .member(member)
                .title("Inquiry Title")
                .content("Inquiry Content")
                .date(LocalDate.now())
                .type(InquiryType.DETAILS)
                .isSecret(false)
                .build();

        when(inquiryService.getInquiryByProductIdAndInquiryId(productId, inquiryId)).thenReturn(inquiry);

        // when
        String viewName = inquiryViewController.getInquiryById(productId, inquiryId, model);

        // then
        assertThat(viewName).isEqualTo("inquiry/detail");
        verify(model).addAttribute("inquiry", inquiry);
        verify(model).addAttribute("productId", productId);
        verify(model).addAttribute("nickname", inquiry.getMember().getNickname());
        verify(model).addAttribute("userEmail", currentUserEmail);
    }
}