// 초기 상태: 입력창과 전송 버튼 비활성화
document.getElementById('chatbot-user-input').disabled = true;
document.getElementById('chatbot-send-btn').disabled = true;

// 메시지 전송 기능 제거
function sendMessage() {
    console.warn("현재는 버튼으로만 질문할 수 있습니다.");
}

// 추천 질문 버튼 클릭 이벤트
document.querySelectorAll('.chatbot-question-btn').forEach(button => {
    button.addEventListener('click', function() {
        processMessage(this.textContent);
    });
});

// 메시지 및 응답 처리
function processMessage(message) {
    appendMessage(message, 'user');
    fetch('/api/v1/chatbot', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `message=${encodeURIComponent(message)}`
    })
    .then(response => response.json())
    .then(data => {
        appendMessage(data.response, 'bot');
    })
    .catch(error => {
        appendMessage("에러가 발생했습니다.", 'bot');
        console.error('Error:', error);
    });
}

// 메시지 추가
function appendMessage(message, sender) {
    const chatbox = document.getElementById('chatbot-content');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender}`;
    messageDiv.textContent = message;
    chatbox.appendChild(messageDiv);
    chatbox.scrollTop = chatbox.scrollHeight;
}

// 입력창 활성화 함수 (나중에 상담사 기능을 위한 준비)
function enableUserInput() {
    const userInput = document.getElementById('chatbot-user-input');
    const sendBtn = document.getElementById('chatbot-send-btn');
    
    userInput.disabled = false;
    userInput.placeholder = "상담사와 채팅을 시작하세요";
    sendBtn.disabled = false;
    console.log("입력창과 전송 버튼이 활성화되었습니다.");
}
