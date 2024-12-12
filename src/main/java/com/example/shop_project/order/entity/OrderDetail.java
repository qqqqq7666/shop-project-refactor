package com.example.shop_project.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@SuperBuilder
@Getter
public class OrderDetail extends OrderBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Long quantity;
    @Column(nullable = false)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "order_no", nullable = false)
    private Order order;

    // product

    public void setOrder (Order order){
        this.order = order;
    }
}
