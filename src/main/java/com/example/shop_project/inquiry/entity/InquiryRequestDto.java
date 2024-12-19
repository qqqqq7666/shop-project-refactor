package com.example.shop_project.inquiry.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryRequestDto {
    private Long productId; // 상품 ID

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title; // 문의 제목

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content; // 문의 내용

    @NotNull(message = "문의 유형은 필수 입력 항목입니다.")
    private InquiryType type; // 문의 유형
}