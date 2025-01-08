// chatbotSetup.js
document.addEventListener('DOMContentLoaded', function () {
    // 챗봇 아이콘 및 위젯 추가
    const chatbotHtml = `
        <!-- 챗봇 아이콘 -->
        <div id="chatbot-icon"></div>
        
        <!-- 챗봇 위젯 -->
        <div id="chatbot-widget" style="display: none; position: fixed; bottom: 80px; right: 20px; width: 350px; height: 500px; border: 1px solid #ccc; border-radius: 10px; background-color: #fff; z-index: 1000; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); overflow: hidden;">
            <div id="chatbot-header" style="background-color: #4b0082; color: white; padding: 10px; text-align: center;">
                챗봇
            </div>
            <div id="chatbot-questions" style="padding: 10px;">
                <button class="chatbot-question-btn">배송 기간은 얼마나 되나요?</button>
                <button class="chatbot-question-btn">환불 정책은 어떻게 되나요?</button>
                <button id="connect-admin-btn" class="chatbot-question-btn">상담사 연결</button>
            </div>
            <div id="chatbot-content" style="height: 280px; overflow-y: auto; border-top: 1px solid #ccc; padding: 10px; background-color: #f9f9f9;"></div>
            <div id="chatbot-input" style="padding: 10px; display: flex; border-top: 1px solid #ccc;">
                <input type="text" id="chatbot-user-input" placeholder="메시지를 입력하세요" style="flex: 1; padding: 5px;">
                <button id="chatbot-send-btn">전송</button>
            </div>
        </div>
    `;

    // HTML을 body에 추가
    document.body.insertAdjacentHTML('beforeend', chatbotHtml);

    // 챗봇 관련 스크립트 로드
    const chatbotScript1 = document.createElement('script');
    chatbotScript1.src = '/chatbot/js/userChat.js';
    document.body.appendChild(chatbotScript1);

    const chatbotScript2 = document.createElement('script');
    chatbotScript2.src = '/chatbot/js/chatbotWork.js';
    document.body.appendChild(chatbotScript2);

    // 챗봇 열기/닫기 기능
    const chatbotIcon = document.getElementById('chatbot-icon');
    const chatbotWidget = document.getElementById('chatbot-widget');

    chatbotIcon.addEventListener('click', function () {
        if (chatbotWidget.classList.contains('open')) {
            chatbotWidget.classList.remove('open');
            setTimeout(() => chatbotWidget.style.display = 'none', 300); // 애니메이션 후 숨김
        } else {
            chatbotWidget.style.display = 'block';
            setTimeout(() => chatbotWidget.classList.add('open'), 10); // 애니메이션 적용
        }
    });

    // ESC 키로 챗봇 닫기
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && chatbotWidget.classList.contains('open')) {
            chatbotWidget.classList.remove('open');
            setTimeout(() => chatbotWidget.style.display = 'none', 300);
        }
    });
});
