package com.example.shop_project.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shop_project.chatbot.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	private final ChatService chatService;
	@GetMapping()
    public String adminDashboard(Model model) {
		model.addAttribute("rooms", chatService.getWaitingOrInProgressRooms());
        return "admin/adminMain"; // 관리자 메인 페이지
    }
}
