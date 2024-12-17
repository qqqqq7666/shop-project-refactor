package com.example.shop_project.inquiry.repository;

import com.example.shop_project.inquiry.entity.Inquiry;
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
}