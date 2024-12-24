package com.example.shop_project.inquiry.service;

import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.inquiry.repository.InquiryRepository;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // 문의 생성
    public Inquiry createInquiry(Long productId, InquiryRequestDto dto, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보를 찾을 수 없습니다: " + userEmail));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 상품을 찾을 수 없습니다: " + productId));

        // Inquiry 생성
        Inquiry inquiry = Inquiry.builder()
                .product(product) // 상품
                .content(dto.getContent())
                .title(dto.getTitle())
                .type(dto.getType())
                .isSecret(dto.getIsSecret()) // 비밀글 여부
                .date(LocalDate.now())
                .member(member) // 회원
                .build();

        return inquiryRepository.save(inquiry);
    }


    // 특정 상품의 문의 목록 조회
    public List<Inquiry> getInquiriesByProductId(Long productId, String currentUserEmail) {
        List<Inquiry> inquiries = inquiryRepository.findByProduct_ProductId(productId);

        // 비밀글 처리
        for (Inquiry inquiry : inquiries) {
            if (inquiry.isSecret() && !inquiry.getMember().getEmail().equals(currentUserEmail)) {
                inquiry.setTitle("비밀글입니다");
                inquiry.setContent("비밀글입니다");
            }
        }

        return inquiries;
    }

    // 특정 상품의 특정 문의 개별 조회
    public Inquiry getInquiryByProductIdAndInquiryId(Long productId, Long inquiryId) {
        return inquiryRepository.findByIdAndProduct_ProductId(inquiryId, productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 상품 ID와 문의 ID에 대한 데이터를 찾을 수 없습니다. 상품 ID: " + productId + ", 문의 ID: " + inquiryId));
    }

    // 전체 문의 목록 조회(관리자용)
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    // 문의 삭제
    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 문의를 찾을 수 없습니다: " + id);
        }
        inquiryRepository.deleteById(id);
    }

    // 특정 회원의 문의 목록 조회
    public List<Inquiry> getInquiriesByMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다: " + email));
        return inquiryRepository.findByMemberId(member.getId());
    }
}