package com.example.shop_project.product.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponseDto {
    private Long productId;
    private String categoryName;
    private String productName;
    private String description;
    private List<String> imageUrls;
    private List<ProductOptionDto> options;



}
