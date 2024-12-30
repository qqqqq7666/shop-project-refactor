package com.example.shop_project.chatbot;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomId;
    private String sender;  // "user" or "admin"
    private String content; // 보낼 메시지

}
