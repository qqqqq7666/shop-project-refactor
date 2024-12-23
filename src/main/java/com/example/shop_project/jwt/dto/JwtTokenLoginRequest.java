package com.example.shop_project.jwt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class JwtTokenLoginRequest {
	private final String email;
	private final String password;
}
