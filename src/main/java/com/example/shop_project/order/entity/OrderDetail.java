package com.example.shop_project.order.entity;

import com.example.shop_project.BaseEntity;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.entity.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    public void assignOrderToCreate(Order order){
        this.order = order;
    }
}
