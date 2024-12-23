package com.example.shop_project.order.dto;

import com.example.shop_project.product.entity.Size;
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
    @NotNull
    private Long productId;
//    @NotNull
    private Size size;
//    @NotNull
    private String color;
    private Long quantity;
}
