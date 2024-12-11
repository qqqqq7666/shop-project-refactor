package com.example.shop_project.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class OrderDetail {
    @Id
    private Long orderDetailId;
    @Positive
    private Long price;
    @Positive
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "order_no")
    private Order order;

    // product
}
