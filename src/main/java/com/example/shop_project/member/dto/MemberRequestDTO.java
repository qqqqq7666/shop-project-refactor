package com.example.shop_project.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberRequestDTO {
	@NotBlank(message = "이메일은 필수 입력 항목입니다")
	@Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
	private String email;
	
	@NotBlank(message = "비밀번호는 필수 입력 항목입니다")
	@Pattern(
	        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
	        message = "비밀번호는 최소 8글자이며 대소문자, 숫자, 특수문자(@$!%*?&)를 최소 하나씩 포함해야 합니다"
	    )
    private String password;
	
	@NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다")
    private String confirmPassword;
	
    @NotBlank(message = "이름은 필수 입력 항목입니다")
    private String name;
    
    @NotBlank(message = "닉네임은 필수 입력 항목입니다")
    private String nickname;
    
    @NotBlank(message = "전화번호는 필수 입력 항목입니다")
    @Pattern(
            regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
            message = "전화번호는 올바른 형식이어야 합니다 (예: 010-1234-5678)"
        )
    private String phone;

    @NotBlank(message = "주소는 필수 입력 항목입니다")
    private String postNo;
	private String address;
	private String addressDetail;
}
