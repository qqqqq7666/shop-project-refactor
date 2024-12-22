package com.example.shop_project.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	USER("ROLE_USER", "일반 회원"),
    ADMIN("ROLE_ADMIN", "관리자");
	
	private final String key;
    private final String title;
	
//	Role(String key, String title) {
//        this.key = key;
//        this.title = title;
//    }
	
	public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }
}
