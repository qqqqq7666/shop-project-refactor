package com.example.shop_project;

import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.InquiryType;
import com.example.shop_project.inquiry.repository.InquiryRepository;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InquiryControllerTest {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("문의 생성")
    void createInquiry() {
        // given
        Product product = Product.builder()
                .categoryName("의류")
                .productName("테스트 상품")
                .description("테스트 상품 설명")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .price(10000)
                .build();
        Product savedProduct = productRepository.save(product);

        InquiryRequestDto requestDto = InquiryRequestDto.builder()
                .nickname("nickname1")
                .title("title1")
                .content("content1")
                .type(InquiryType.SIZE)
                .build();

        // when
        Inquiry savedInquiry = inquiryService.createInquiry(savedProduct.getProductId(), requestDto);

        // then
        Inquiry foundInquiry = inquiryRepository.findById(savedInquiry.getId())
                .orElseThrow(() -> new EntityNotFoundException("문의가 존재하지 않습니다."));

        assertEquals(savedInquiry.getId(), foundInquiry.getId());
        assertEquals(savedInquiry.getProduct().getProductId(), foundInquiry.getProduct().getProductId());
        assertEquals(savedInquiry.getNickname(), foundInquiry.getNickname());
        assertEquals(savedInquiry.getTitle(), foundInquiry.getTitle());
        assertEquals(savedInquiry.getContent(), foundInquiry.getContent());
    }

    @Test
    @DisplayName("문의 조회")
    void getInquiryById() {
        // given
        Product product = Product.builder()
                .categoryName("의류")
                .productName("테스트 상품")
                .description("테스트 상품 설명")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .price(10000)
                .build();
        Product savedProduct = productRepository.save(product);

        Inquiry inquiry = Inquiry.builder()
                .product(savedProduct)
                .nickname("nickname1")
                .title("title1")
                .content("content1")
                .date(LocalDate.now())
                .type(InquiryType.SIZE)
                .build();
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // when
        Inquiry foundInquiry = inquiryService.getInquiryById(savedInquiry.getId());

        // then
        assertNotNull(foundInquiry);
        assertEquals(savedInquiry.getId(), foundInquiry.getId());
        assertEquals(savedInquiry.getProduct().getProductId(), foundInquiry.getProduct().getProductId());
        assertEquals(savedInquiry.getNickname(), foundInquiry.getNickname());
        assertEquals(savedInquiry.getTitle(), foundInquiry.getTitle());
        assertEquals(savedInquiry.getContent(), foundInquiry.getContent());
        assertEquals(savedInquiry.getDate(), foundInquiry.getDate());
    }

    @Test
    @DisplayName("문의 삭제")
    void deleteInquiry() {
        // given
        Product product = Product.builder()
                .categoryName("의류")
                .productName("테스트 상품")
                .description("테스트 상품 설명")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .price(10000)
                .build();
        Product savedProduct = productRepository.save(product);

        Inquiry inquiry = Inquiry.builder()
                .product(savedProduct)
                .nickname("nickname1")
                .title("title1")
                .content("content1")
                .date(LocalDate.now())
                .type(InquiryType.SIZE)
                .build();
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        // when
        inquiryService.deleteInquiry(savedInquiry.getId());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            inquiryRepository.findById(savedInquiry.getId())
                    .orElseThrow(() -> new EntityNotFoundException("문의가 존재하지 않습니다."));
        });
    }
}
