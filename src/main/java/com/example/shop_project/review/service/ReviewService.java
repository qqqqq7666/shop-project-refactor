package com.example.shop_project.review.service;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
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
import java.util.List;
import java.util.stream.Collectors;

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

    // 상품 별 리뷰 조회
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

    // 상품 별 별점 평균 조회
    public Double getAverageStarsByProductId(Long productId) {
        Double averageStars = reviewRepository.averageStarsByProductId(productId);
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
                        .color(review.getOrderDetail().getColor())
                        .size(review.getOrderDetail().getSize())
                        .build();
    }
}
