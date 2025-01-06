package com.example.shop_project.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long OptionId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    private Size size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int stockQuantity;
}
