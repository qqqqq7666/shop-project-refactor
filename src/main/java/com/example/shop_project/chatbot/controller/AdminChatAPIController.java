package com.example.shop_project.chatbot.controller;

import com.example.shop_project.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    // 상담 종료
    @PostMapping("/end")
    public ResponseEntity<String> endChat(@RequestParam("roomId") String roomId) {
        chatService.endChat(roomId);
        return ResponseEntity.ok("ended");
    }
}
