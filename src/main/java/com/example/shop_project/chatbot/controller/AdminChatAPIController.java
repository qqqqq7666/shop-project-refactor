package com.example.shop_project.chatbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop_project.chatbot.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat/admin")
@RequiredArgsConstructor
public class AdminChatAPIController {
	private final ChatService chatService;

    @GetMapping("/assign")
    public ResponseEntity<String> assignAdminToRoom(@RequestParam("roomId") String roomId,
                                                    @RequestParam("adminId") String adminId) {
        chatService.assignAdmin(roomId, adminId);
        return ResponseEntity.ok("assigned");
    }
}
