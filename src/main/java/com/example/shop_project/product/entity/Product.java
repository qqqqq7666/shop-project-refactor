package com.example.shop_project.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private String categoryName;

    @NotBlank
    @Size(max = 20, message = "상품명은 최대 20자까지 가능합니다.")
    @Column(nullable = false, length = 20)
    private String productName;

    @NotBlank
    @Size(max = 100, message = "상품 상세 내용은 최대 100자까지 가능합니다.")
    @Column(nullable = false, length = 100)
    private String description;

    /*
    private String condition;
     */

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private int viewCount;
    private int salesCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

}
