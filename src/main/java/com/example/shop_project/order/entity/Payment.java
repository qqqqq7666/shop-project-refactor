package com.example.shop_project.order.entity;

import com.example.shop_project.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@Getter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @Column(nullable = false)
    private String memberName;
    @Column(nullable = false)
    private Long amount;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PayMethod payMethod;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PayStatus payStatus;

    @OneToOne
    @JoinColumn(name = "order_no")
    private Order order;

    public void assignOrderToCreate(Order order){
        this.order = order;
    }
}
