package com.example.shop_project.member.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MemberRequestDTO {
	private String email;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String phone;

    private String postNo;
	private String address;
	private String addressDetail;
}
