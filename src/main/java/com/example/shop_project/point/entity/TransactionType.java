package com.example.shop_project.point.entity;

public enum TransactionType {
    MONTHLY_GIVE("매월 적립 혜택"), REFUND("주문 취소"), PURCHASE("구매 적립"),
    RATE("리뷰 적립"), USE("사용");

    private final String value;

    TransactionType(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
