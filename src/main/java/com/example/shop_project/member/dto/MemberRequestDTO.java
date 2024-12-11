package com.example.shop_project.member.dto;

import lombok.Data;

@Data
public class MemberRequestDTO {
	private String email;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String phone;
}
