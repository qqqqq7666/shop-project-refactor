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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final OrderDetailRepository orderDetailRepository;

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

    public List<ReviewResponseDto> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return toReviewResponseDtoList(reviews);
    }

    private List<ReviewResponseDto> toReviewResponseDtoList(List<Review> reviews) {
        return reviews.stream()
                .map(entity -> ReviewResponseDto.builder()
                        .reviewId(entity.getReviewId())
                        .stars(entity.getStars())
                        .content(entity.getContent())
                        .date(entity.getDate())
                        .nickname(entity.getMember().getNickname())
                        .color(entity.getOrderDetail().getColor())
                        .size(entity.getOrderDetail().getSize())
                        .build())
                .collect(Collectors.toList());
    }
}
