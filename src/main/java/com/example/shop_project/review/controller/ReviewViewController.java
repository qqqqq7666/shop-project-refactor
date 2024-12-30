package com.example.shop_project.review.controller;

import com.example.shop_project.review.dto.ReviewRequestDto;
import com.example.shop_project.review.dto.ReviewResponseDto;
import com.example.shop_project.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewViewController {
    private final ReviewService reviewService;

    // 리뷰 작성 페이지
    @GetMapping("/review/create")
    public String createReview() {
        return "review/reviewCreate";
    }

    // 리뷰 저장
    @PostMapping("/review/create")
    public String createReview(@Valid @ModelAttribute ReviewRequestDto reviewRequestDto) {
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.saveReview(reviewRequestDto, memberEmail);
        return "redirect:/order";
    }

    // 리뷰 목록 페이지
    @GetMapping("/products/{productId}/reviews")
    public String reviewList(@PathVariable Long productId,
                             @RequestParam(defaultValue = "date") String sort,
                             @RequestParam(defaultValue = "0") int page, Model model){
        Page<ReviewResponseDto> reviews = reviewService.getReviewsByProductId(productId, sort, page);
        Double averageStars = reviewService.getAverageStarsByProductId(productId);

        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("totalPages", reviews.getTotalPages());
        model.addAttribute("currentPage", reviews.getNumber());
        model.addAttribute("sortOption", sort);
        model.addAttribute("averageStars", averageStars);

        return "review/reviewList";
    }
}
