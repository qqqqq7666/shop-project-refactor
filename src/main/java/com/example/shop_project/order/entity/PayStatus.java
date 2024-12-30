package com.example.shop_project.order.entity;

public enum PayStatus {
    SUCCESS("결제 완료"), CANCEL("결제 취소"), FAIL("결제 실패");

    private final String value;

    PayStatus(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
