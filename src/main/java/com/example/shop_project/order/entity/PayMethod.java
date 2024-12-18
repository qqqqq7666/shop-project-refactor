package com.example.shop_project.order.entity;

public enum PayMethod {
    KG("일반 카드 결제"), KAKAO("카카오 페이"), TOSS("토스 페이"), NAVER("네이버 페이");

    private final String value;

    PayMethod(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
