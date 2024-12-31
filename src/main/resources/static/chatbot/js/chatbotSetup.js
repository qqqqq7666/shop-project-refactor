// chatbotSetup.js
document.addEventListener('DOMContentLoaded', function () {
    const chatbotHtml = `
    <div id="chatbot-widget">
        <div id="chatbot-header">챗봇</div>
        <div id="chatbot-questions">
            <button class="chatbot-question-btn">배송 기간은 얼마나 되나요?</button>
            <button class="chatbot-question-btn">환불 정책은 어떻게 되나요?</button>
            <button class="chatbot-question-btn">어떤 결제 수단을 사용할 수 있나요?</button>
            <button id="connect-admin-btn" class="chatbot-question-btn">상담사 연결</button>
        </div>
        <div id="chatbot-content"></div>
        <div id="chatbot-input">
            <input type="text" id="chatbot-user-input" placeholder="상담사와 대화를 원한다면 연결해주세요" disabled>
            <button id="chatbot-send-btn" disabled>전송</button>
        </div>
    </div>`;

    // 챗봇을 body에 추가
    document.body.insertAdjacentHTML('beforeend', chatbotHtml);

    // 챗봇 관련 스크립트 로드
    const chatbotScript1 = document.createElement('script');
    chatbotScript1.src = '/chatbot/js/userChat.js';
    document.body.appendChild(chatbotScript1);

    const chatbotScript2 = document.createElement('script');
    chatbotScript2.src = '/chatbot/js/chatbotWork.js';
    document.body.appendChild(chatbotScript2);
});
