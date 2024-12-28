package com.example.shop_project.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    private Long orderDetailId;

    @Min(1)
    @Max(5)
    private int stars;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(min = 20, max = 500, message = "리뷰 내용은 최소 20자 이상, 최대 500자 이하로 작성해야 합니다.")
    private String content;
}