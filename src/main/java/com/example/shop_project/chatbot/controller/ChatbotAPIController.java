package com.example.shop_project.chatbot.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shop_project.chatbot.service.ChatbotService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotAPIController {
	private final ChatbotService chatbotService;

    @PostMapping
    public ResponseEntity<Map<String, String>> handleChatbot(@RequestParam("message") String message) {
        // 챗봇 로직
        String botResponse = chatbotService.getResponse(message);

        // JSON 형태의 응답 생성
        Map<String, String> json = new HashMap<>();
        json.put("response", botResponse);

        // 200 OK 응답
        return ResponseEntity.ok(json);
    }
}
