package com.example.shop_project.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ImageId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotBlank
    private String imageUrl; // 이미지 파일 경로 또는 URL

    @Column(nullable = false)
    private int sortOrder; // 이미지 순서 (1, 2, 3...)
}
