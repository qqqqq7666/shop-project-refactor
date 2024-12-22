package com.example.shop_project.product.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductResponseDto {
    private Long productId;
    private String categoryName;
    private String productName;
    private String description;
    private int price;
    private int salesCount;
    private int viewCount;
    private List<String> imageUrls;
    private List<ProductOptionDto> options;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
