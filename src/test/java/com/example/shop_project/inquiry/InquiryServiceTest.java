package com.example.shop_project.inquiry;

import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.InquiryType;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.inquiry.repository.InquiryRepository;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InquiryServiceTest {

    @InjectMocks
    private InquiryService inquiryService;

    @Mock
    private InquiryRepository inquiryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContextHolder securityContextHolder;

    private String userEmail = "test@example.com";
    private String adminEmail = "admin@example.com";
    private Long productId = 1L;
    private Long inquiryId = 1L;
    private Member member;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mock member and product
        member = new Member();
        member.setEmail(userEmail);
        member.setNickname("Test User");

        product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");
    }

    @Test
    @DisplayName("문의 생성")
    void testCreateInquiry() {
        // given
        InquiryRequestDto dto = new InquiryRequestDto(null, "Test title", "Test content", InquiryType.DETAILS, false);
        when(memberRepository.findByEmail(userEmail)).thenReturn(Optional.of(member));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(new Inquiry());

        // when
        Inquiry createdInquiry = inquiryService.createInquiry(productId, dto, userEmail);

        // then
        assertNotNull(createdInquiry);
        verify(inquiryRepository, times(1)).save(any(Inquiry.class));
    }

    // TODO 특정 상품의 문의 목록 조회

    @Test
    @DisplayName("특정 문의 개별 조회")
    void testGetInquiryByProductIdAndInquiryId() {
        // given
        Inquiry inquiry = new Inquiry();
        inquiry.setId(inquiryId);
        when(inquiryRepository.findByIdAndProduct_ProductId(inquiryId, productId)).thenReturn(Optional.of(inquiry));

        // when
        Inquiry result = inquiryService.getInquiryByProductIdAndInquiryId(productId, inquiryId);

        // then
        assertNotNull(result);
        verify(inquiryRepository, times(1)).findByIdAndProduct_ProductId(inquiryId, productId);
    }

    @Test
    @DisplayName("문의 삭제")
    void testDeleteInquiry() {
        // given
        Inquiry inquiry = new Inquiry();
        inquiry.setId(inquiryId);
        when(inquiryRepository.existsById(inquiryId)).thenReturn(true);

        // when
        inquiryService.deleteInquiry(inquiryId);

        // then
        verify(inquiryRepository, times(1)).deleteById(inquiryId);
    }

    @Test
    @DisplayName("문의 삭제 시 해당 문의가 존재하지 않으면 예외 발생")
    void testDeleteInquiry_NotFound() {
        // given
        when(inquiryRepository.existsById(inquiryId)).thenReturn(false);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inquiryService.deleteInquiry(inquiryId);
        });

        assertEquals("해당 ID의 문의를 찾을 수 없습니다: " + inquiryId, exception.getMessage());
    }

    @Test
    @DisplayName("특정 회원의 문의 목록 조회")
    void testGetInquiriesByMember() {
        // given
        Inquiry inquiry1 = new Inquiry();
        inquiry1.setMember(member);

        Inquiry inquiry2 = new Inquiry();
        inquiry2.setMember(member);

        List<Inquiry> inquiries = List.of(inquiry1, inquiry2);
        when(memberRepository.findByEmail(userEmail)).thenReturn(Optional.of(member));
        when(inquiryRepository.findByMemberId(member.getId())).thenReturn(inquiries);

        // when
        List<Inquiry> result = inquiryService.getInquiriesByMember(userEmail);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(inquiryRepository, times(1)).findByMemberId(member.getId());
    }

    @Test
    @DisplayName("특정 회원의 문의 개수 조회")
    void testGetInquiryCountByMember() {
        // given
        when(inquiryRepository.countByMemberEmail(userEmail)).thenReturn(5L);

        // when
        Long count = inquiryService.getInquiryCountByMember(userEmail);

        // then
        assertEquals(5L, count);
        verify(inquiryRepository, times(1)).countByMemberEmail(userEmail);
    }
}
