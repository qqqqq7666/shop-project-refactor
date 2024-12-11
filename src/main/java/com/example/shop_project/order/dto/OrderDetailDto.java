package com.example.shop_project.order.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailDto {
    @Positive
    private Long price;
    @Positive
    private Long quantity;
}
