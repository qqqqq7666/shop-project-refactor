package com.example.shop_project.review.entity;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.entity.OrderDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(nullable = false)
    private int stars;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDate date; // 작성 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 리뷰 작성자

    @OneToOne
    @JoinColumn(name = "order_detail_id", unique = true, nullable = false)
    private OrderDetail orderDetail; // 리뷰 상품

    @Column(name = "product_id", nullable = false)
    private Long productId;

}
