package com.example.shop_project.inquiry.entity;

public enum InquiryType {
    SIZE("사이즈"),
    SHIPPING("배송"),
    RESTOCK("재입고"),
    DETAILS("상품상세");

    private final String koreanName;

    InquiryType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}