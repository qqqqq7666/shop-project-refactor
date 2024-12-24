package com.example.shop_project.point.entity;

public enum SaveReason {
    MONTHLY_GIVE("매월 적립 혜택"), REFUND("주문 취소"), PURCHASE("구매 혜택"), RATE("리뷰 혜택");

    private final String value;

    SaveReason(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
