package com.example.shop_project.chatbot.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.shop_project.chatbot.ChatMessage;
import com.example.shop_project.chatbot.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    // 메시지 저장 및 처리
	private final ChatService chatService;

    @MessageMapping("/chat/send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        // 메시지 처리 로직 (DB 저장, roomId 체크 등)
    	chatService.saveMessage(message);
        return message; // 반환 시 구독중인 사용자에게 전송됨
    }
}
