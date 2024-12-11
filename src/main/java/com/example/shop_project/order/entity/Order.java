package com.example.shop_project.order.entity;

import com.example.shop_project.order.dto.OrderRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@SuperBuilder
@NoArgsConstructor
public class Order extends OrderBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderNo;
    @Column(nullable = false)
    private Long totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayMethod payMethod;
    // 직접 입력을 위해 String
    @Column(nullable = false)
    private String requirement;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String addressDetail;
    @Column(nullable = false, length = 5)
    private String postNo;
    // member

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    public void updateOrder(OrderRequestDto orderRequestDto){
        totalPrice = orderRequestDto.getTotalPrice();
        orderStatus = orderRequestDto.getOrderStatus();
        payMethod = orderRequestDto.getPayMethod();
        address = orderRequestDto.getAddress();
        addressDetail = orderRequestDto.getAddressDetail();
        postNo = orderRequestDto.getPostNo();
    }

}
