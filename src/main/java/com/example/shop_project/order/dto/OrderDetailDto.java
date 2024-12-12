package com.example.shop_project.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailDto {
    @Positive
    @NotNull
    private Long price;
    @Positive
    @NotNull
    private Long quantity;
    @NotNull
    private Long productId;
}
