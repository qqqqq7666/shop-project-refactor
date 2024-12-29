package com.example.shop_project.order.entity;

import com.example.shop_project.BaseEntity;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.dto.OrderRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@SuperBuilder
@NoArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNo;
    @Column(nullable = false)
    private Long totalPrice;    // 모든 상품상세 quantity * price 합
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
    // 직접 입력을 위해 String
    @Column(nullable = false)
    private String requirement;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String addressDetail;
    @Column(nullable = false, length = 5)
    private String postNo;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = false)
    private String addressee;
    @Column(nullable = false)
    private Boolean isPaidWithPoint;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    public void updateOrder(OrderRequestDto orderRequestDto) {
        totalPrice = orderRequestDto.getTotalPrice();
        orderStatus = orderRequestDto.getOrderStatus();
        address = orderRequestDto.getAddress();
        addressDetail = orderRequestDto.getAddressDetail();
        postNo = orderRequestDto.getPostNo();
    }

    public void updateStatus(OrderStatus status) {
        orderStatus = status;
    }


    public void assignOrderToOrderDetail() {
        orderDetailList.forEach(orderDetail -> {
            orderDetail.assignOrderToCreate(this);
        });
    }
}
