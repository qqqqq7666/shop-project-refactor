package com.example.shop_project.order.entity;

import com.example.shop_project.BaseEntity;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.Size;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@SuperBuilder
@Getter
public class OrderDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private Size size;
    private String color;

    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // product

    public void assignOrderCreate(Order order){
        this.order = order;
    }
}
