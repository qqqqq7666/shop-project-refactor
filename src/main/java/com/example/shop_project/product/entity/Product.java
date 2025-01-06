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

    @JoinColumn(name = "category_id", nullable = false)
    private Long categoryId;

    @NotBlank
    @Size(max = 20, message = "상품명은 최대 50자까지 가능합니다.")
    @Column(nullable = false, length = 50)
    private String productName;

    @NotBlank
    @Size(max = 100, message = "상품 상세 내용은 최대 500자까지 가능합니다.")
    @Column(nullable = false, length = 500)
    private String description;

    /*
    private String condition;
     */

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int price;

    @Column(name = "view_count")
    private int viewCount;
    private int salesCount;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @JoinColumn(name = "nickname")
    private String nickname;

}
