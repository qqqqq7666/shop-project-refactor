package com.example.shop_project.order.entity;

public enum PG {
    KG("일반 카드 결제"), KAKAO("카카오 페이"), TOSS("토스 페이"), NAVER("네이버 페이");

    private final String value;

    PG(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
