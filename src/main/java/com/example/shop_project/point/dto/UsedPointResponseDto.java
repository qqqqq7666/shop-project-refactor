package com.example.shop_project.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UsedPointResponseDto {
    @NotNull
    private Long orderNo;
    @NotNull
    @Positive
    private Integer amount;
    @NotNull
    private LocalDateTime createdDate;
}
