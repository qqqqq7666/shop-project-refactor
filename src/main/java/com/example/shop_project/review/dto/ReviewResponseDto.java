package com.example.shop_project.review.dto;

import com.example.shop_project.product.entity.Size;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private int stars;
    private String content;
    private LocalDate date;
    private String nickname; // 리뷰 작성자의 닉네임
    private Long productId;
    private String productName;
    private String color;
    private Size size;

}
