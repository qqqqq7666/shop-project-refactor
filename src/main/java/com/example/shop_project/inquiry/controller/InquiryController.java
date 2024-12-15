package com.example.shop_project.inquiry.controller;

import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // [1] HTML 페이지 렌더링 (문의 작성 폼 + 목록 표시)
    @GetMapping
    public String showInquiries(Model model) {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        model.addAttribute("inquiries", inquiries); // 목록 데이터를 모델에 추가
        return "inquiry/list";  // inquiry.html 반환
    }

    // [2] 문의 생성 (폼에서 전송된 데이터 처리)

    // 문의 작성 페이지
    @GetMapping("/create")
    public String showCreatePage() {
        return "inquiry/create"; // create.html 반환
    }

    @PostMapping
    public String createInquiry(@Valid @ModelAttribute InquiryRequestDto dto) {
        System.out.println("Received InquiryRequestDto: " + dto);
        Inquiry savedInquiry = inquiryService.createInquiry(dto);
        return "redirect:/inquiries";
    }

    // [3] 개별 문의 조회 (REST API)
    @GetMapping("/{id}")
    public String getInquiryById(@PathVariable Long id, Model model) {
        Inquiry inquiry = inquiryService.getInquiryById(id);
        model.addAttribute("inquiry", inquiry);
        return "inquiry/detail";
    }

    // [4] 문의 삭제 (REST API)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}