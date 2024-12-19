package com.example.shop_project.member;

public enum Membership {
	BRONZE("브론즈", 0),
    SILVER("실버", 200000),
    GOLD("골드", 500000),
    DIAMOND("다이아", 1000000);

    private final String name;
    private final int requiredPoints;

    Membership(String name, int requiredPoints) {
        this.name = name;
        this.requiredPoints = requiredPoints;
    }

    public String getName() {
        return name;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }
}
