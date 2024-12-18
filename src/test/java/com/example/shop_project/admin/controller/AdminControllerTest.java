package com.example.shop_project.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	@WithMockUser(username = "admin", authorities = {"ADMIN"})
	void ADMIN_권한으로_페이지_접근_성공() throws Exception {
	    mockMvc.perform(get("/admin"))
	            .andExpect(status().isOk()); // 200 OK
	}

	@Test
	@WithMockUser(username = "user", authorities = {"USER"})
	void USER_권한으로_페이지_접근_실패() throws Exception {
	    mockMvc.perform(get("/admin"))
	            .andExpect(status().isForbidden()); // 403 Forbidden
	}
}
