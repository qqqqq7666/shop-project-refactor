package com.example.shop_project.order.entity;

import org.springframework.http.HttpStatus;

public enum OrderStatus {
    NEW("신규주문"), PREPARING("배송 준비 중"), IN_SHIPPING("배송중"), ARRIVE("배송 완료"),
    CANCEL("취소 요청"), REFUND("환불 요청"), EXCHANGE("교환 요청");

    private final String value;

    private OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
