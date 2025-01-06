package com.example.shop_project.inquiry.controller;

import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.service.InquiryService;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import com.example.shop_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products/{productId}/inquiries")
@RequiredArgsConstructor
public class InquiryViewController {

    private final InquiryService inquiryService;
    private final ProductRepository productRepository;
    private final ProductService productService;

    // ROLE_ADMIN 권한 확인
    private boolean isAdmin() {
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    // 특정 상품의 문의 목록 페이지 렌더링
    @GetMapping
    public String showInquiriesByProduct(@PathVariable("productId") Long productId, Model model) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Inquiry> inquiries = inquiryService.getInquiriesByProductId(productId, currentUserEmail);

        boolean isAdmin = isAdmin();

        List<Map<String, Object>> inquiryDetails = inquiries.stream().map(inquiry -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("inquiry", inquiry);
            detail.put("canView", isAdmin || !inquiry.isSecret() || inquiry.getMember().getEmail().equals(currentUserEmail));
            return detail;
        }).toList();

        ProductResponseDto product = productService.getProductDetail(productId);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        String formattedPrice = numberFormat.format(product.getPrice());

        model.addAttribute("inquiries", inquiryDetails);
        model.addAttribute("productId", productId);
        model.addAttribute("product", product);
        model.addAttribute("formattedPrice", formattedPrice);

        return "inquiry/list";
    }

    // 특정 상품의 문의 작성 페이지
    @GetMapping("/create")
    public String showCreatePage(@PathVariable("productId") Long productId, Model model) {
        ProductResponseDto product = productService.getProductDetail(productId);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.KOREA);
        String formattedPrice = numberFormat.format(product.getPrice());

        model.addAttribute("productId", productId);
        model.addAttribute("product", product);
        model.addAttribute("formattedPrice", formattedPrice);

        return "inquiry/create";
    }

    // 특정 상품의 문의 생성 처리
    @PostMapping
    public String createInquiry(
            @PathVariable("productId") Long productId,
            @Valid @ModelAttribute InquiryRequestDto dto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        inquiryService.createInquiry(productId, dto, userEmail);

        return "redirect:/products/" + productId + "/inquiries";
    }

    // 특정 상품의 특정 문의 조회
    @GetMapping("/{inquiryId}")
    public String getInquiryById(@PathVariable("productId") Long productId, @PathVariable("inquiryId") Long inquiryId, Model model) {
        Inquiry inquiry = inquiryService.getInquiryByProductIdAndInquiryId(productId, inquiryId);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        String nickname = inquiry.getMember().getNickname();

        model.addAttribute("inquiry", inquiry);
        model.addAttribute("productId", productId);
        model.addAttribute("nickname", nickname);
        model.addAttribute("userEmail", userEmail);

        return "inquiry/detail";
    }

}