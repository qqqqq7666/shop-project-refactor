package com.example.shop_project.order.entity;

public enum PayMethod {
    CARD("카드결제"), CASH("계좌이체");

    private final String value;

    PayMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
