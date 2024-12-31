package com.example.shop_project.review.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.review.dto.ReviewRequestDto;
import com.example.shop_project.review.dto.ReviewResponseDto;
import com.example.shop_project.review.entity.Review;
import com.example.shop_project.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final OrderDetailRepository orderDetailRepository;

    // 리뷰 저장
    @Transactional
    public Review saveReview(ReviewRequestDto reviewRequestDto, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보를 찾을 수 없습니다: " + memberEmail));

        OrderDetail orderDetail = orderDetailRepository.findById(reviewRequestDto.getOrderDetailId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 내역이 존재하지 않습니다."));;

        Review review = Review.builder()
                .stars(reviewRequestDto.getStars())
                .content(reviewRequestDto.getContent())
                .date(LocalDate.now())
                .member(member)
                .orderDetail(orderDetail)
                .productId(orderDetail.getProduct().getProductId())
                .build();

        return reviewRepository.save(review);
    }

    // 회원별 리뷰 조회
    public Page<ReviewResponseDto> getReviewsByMember(String memberEmail, int page) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보를 찾을 수 없습니다: " + memberEmail));

        Pageable pageable = PageRequest.of(page, 10, Sort.by("date").descending());
        Page<Review> reviews = reviewRepository.findByMember(member, pageable);
        return reviews.map(this::toReviewResponseDto);
    }

    // 회원별 리뷰 수 조회
    public int getReviewCountByMember(Member member) {
        return reviewRepository.countByMember(member);
    }

    // 상품별 리뷰 조회
    public Page<ReviewResponseDto> getReviewsByProductId(Long productId, String sort, int page) {
        Sort sortOrder = Sort.by("date").descending(); // 최신순 정렬(기본값)
        if ("highRating".equals(sort)) {
            sortOrder = Sort.by("stars").descending();  // 높은 평점순 정렬
        } else if ("lowRating".equals(sort)) {
            sortOrder = Sort.by("stars").ascending();  // 낮은 평점순 정렬
        }
        Pageable pageable = PageRequest.of(page, 10, sortOrder);

        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(this::toReviewResponseDto);
    }

    // 상품별 별점 평균 조회
    public Double getAverageStarsByProductId(Long productId) {
        Double averageStars = reviewRepository.averageStarsByProductId(productId);
        if (averageStars == null) {
            return 0.0;
        }
        return averageStars;
    }

    // Entity -> ReviewResponseDto 변환 메서드
    private ReviewResponseDto toReviewResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .stars(review.getStars())
                .content(review.getContent())
                .date(review.getDate())
                .nickname(review.getMember().getNickname())
                .productId(review.getProductId())
                .productName(review.getOrderDetail().getProduct().getProductName())
                .color(review.getOrderDetail().getColor())
                .size(review.getOrderDetail().getSize())
                .build();
    }

    public Map<Long, Boolean> getReviewStatusMap(Page<OrderResponseDto> orderPage) {
        Map<Long, Boolean> reviewStatusMap = new HashMap<>();

        for (OrderResponseDto order : orderPage) {
            for (OrderDetail detail : order.getOrderDetailList()) {
                boolean hasReviewed = reviewRepository.existsByOrderDetail(detail);
                reviewStatusMap.put(detail.getOrderDetailId(), hasReviewed);
            }
        }

        return reviewStatusMap;
    }
}
