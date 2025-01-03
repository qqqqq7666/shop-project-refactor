package com.example.shop_project.inquiry.controller;

import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/my-inquiries")
@RequiredArgsConstructor
public class MyInquiryViewController {
    private final InquiryService inquiryService;

    // 회원이 작성한 문의 목록 조회
    @GetMapping
    public String showMyInquiries(Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Inquiry> inquiries = inquiryService.getInquiriesByMember(userEmail);

        for (Inquiry inquiry : inquiries) {
            inquiry.setCommentCount(inquiry.getComments().size());
        }

        model.addAttribute("inquiries", inquiries);

        return "inquiry/myInquiries";
    }
}