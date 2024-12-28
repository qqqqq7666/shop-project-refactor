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

document.getElementById('connect-admin-btn').addEventListener('click', function() {
    // 1) 서버로 상담사 연결 요청(채팅방 생성 or 상담 가능 여부 확인)
    fetch('/api/v1/chat/connection', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ request: 'connectAdmin' })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('상담사 연결에 실패했습니다.');
        }
        return response.json();
    })
    .then(data => {
        // 2) 서버로부터 연결 성공 응답을 받으면, 사용자 입력창 활성화
        enableUserInput(); 
        // 3) 안내 메시지
        appendMessage("상담사 연결이 완료되었습니다. 궁금한 점을 입력하세요.", 'bot');
    })
    .catch(error => {
        console.error(error);
        appendMessage("현재 상담 가능하지 않습니다. 잠시 후 다시 시도해주세요.", 'bot');
    });
});
