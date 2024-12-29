package com.example.shop_project.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsedPointRequestDto {
    @NotNull
    private String email;
    @NotNull
    private Long orderNo;
    @NotNull
    @Positive
    private Integer amount;
}
