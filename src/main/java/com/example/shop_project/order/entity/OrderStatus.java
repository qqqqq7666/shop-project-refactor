package com.example.shop_project.order.entity;

import org.springframework.http.HttpStatus;

public enum OrderStatus {
    NEW("신규주문"), PREPARING("배송 준비 중"), IN_SHIPPING("배송중"), ARRIVE("배송 완료"),
    CANCEL_REQUIRE("취소 요청"), REFUND_REQUIRE("환불 요청"), EXCHANGE_REQUIRE("교환 요청"), FAIL("결제 실패"), CONFIRMED("구매 확정"),
    REFUND("환불 처리 완료"), EXCHANGE("교환 처리 완료");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
