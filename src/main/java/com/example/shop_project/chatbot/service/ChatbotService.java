package com.example.shop_project.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {
    public String getResponse(String message) {
        if(message == null) return "무슨 말씀이신지 잘 모르겠습니다.";

        message = message.trim().toLowerCase();
        switch(message) {
            case "배송 기간은 얼마나 되나요?":
                return "보통 주문 후 3-5일 이내에 배송됩니다.";
            case "환불 정책은 어떻게 되나요?":
                return "제품 수령 후 7일 이내에 환불 요청이 가능합니다.";
            case "어떤 결제 수단을 사용할 수 있나요?":
                return "신용카드, 카카오페이, 네이버페이로 결제 가능합니다.";
            case "상담사 연결":
                return "상담사를 연결해드리겠습니다. 잠시만 기다리세요.";
            default:
                return "죄송합니다. 해당 질문에 대한 답변을 준비하지 못했습니다.";
        }
    }
    

}
