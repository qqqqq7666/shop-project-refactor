package com.example.shop_project.chatbot;

import lombok.Data;

@Data
public class ChatRoom {
	private String roomId;        
    private String userId;        
    private String adminId;       
    private String status;  
}
