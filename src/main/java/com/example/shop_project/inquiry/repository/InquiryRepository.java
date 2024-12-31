package com.example.shop_project.inquiry.repository;

import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 특정 상품의 모든 문의 조회
    List<Inquiry> findByProduct_ProductId(Long productId);

    // 특정 상품의 특정 문의 조회
    Optional<Inquiry> findByIdAndProduct_ProductId(Long inquiryId, Long productId);

    // 특정 사용자가 작성한 모든 문의를 조회
    List<Inquiry> findByMemberId(Long memberId);

    // 특정 회원의 문의 개수 조회
    Long countByMemberEmail(String email);
}