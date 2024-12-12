package com.example.shop_project.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberRequestDTO {
	@NotBlank(message = "이메일은 필수 입력 항목입니다")
	private String email;
	@NotBlank(message = "비밀번호는 필수 입력 항목입니다")
    private String password;
    private String confirmPassword;
    @NotBlank(message = "닉네임은 필수 입력 항목입니다")
    private String nickname;
    @NotBlank(message = "전화번호는 필수 입력 항목입니다")
    private String phone;

    @NotBlank(message = "주소는 필수 입력 항목입니다")
    private String postNo;
	private String address;
	private String addressDetail;
}
