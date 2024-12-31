package com.example.shop_project.inquiry.controller;

import com.example.shop_project.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/inquiries")
@RequiredArgsConstructor
public class InquiryAPIController {

    private final InquiryService inquiryService;

    // 특정 상품의 특정 문의 삭제
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long productId, @PathVariable Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.noContent().build();
    }
}