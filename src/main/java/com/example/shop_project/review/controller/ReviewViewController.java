package com.example.shop_project.review.controller;

import com.example.shop_project.point.dto.SavedPointRequestDto;
import com.example.shop_project.point.service.PointService;
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
    private final PointService pointService;

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

        pointService.createSavedPoint(SavedPointRequestDto.builder()
                .savedPoint(500)
                .saveReason("리뷰 작성 혜택")
                .email(memberEmail)
                .build());

        return "redirect:/order";
    }

    // 리뷰 목록 페이지
    @GetMapping("/products/{productId}/reviews")
    public String reviewList(@PathVariable Long productId, @RequestParam(defaultValue = "0") int page, Model model){
        Page<ReviewResponseDto> reviews = reviewService.getReviewsByProductId(productId, page);
        Double averageStars = reviewService.getAverageStarsByProductId(productId);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("totalPages", reviews.getTotalPages());
        model.addAttribute("currentPage", reviews.getNumber());
        model.addAttribute("averageStars", averageStars);
        return "review/reviewList";
    }
}
