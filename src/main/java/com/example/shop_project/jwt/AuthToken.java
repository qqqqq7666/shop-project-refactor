package com.example.shop_project.jwt;

public interface AuthToken <T> {
	String AUTHORITIES_TOKEN_KEY = "role";
	
	boolean validate();
	
	T getDate();

}
