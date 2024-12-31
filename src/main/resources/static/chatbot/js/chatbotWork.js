// let isCounseling = false;

// 초기 상태: 입력창과 전송 버튼 비활성화
document.getElementById('chatbot-user-input').disabled = true;
document.getElementById('chatbot-send-btn').disabled = true;

// 추천 질문 버튼 클릭 이벤트
document.querySelectorAll('.chatbot-question-btn').forEach(button => {
    button.addEventListener('click', function() {
		
		if (window.isCounseling) {
            // 상담 중이면 버튼식 자동응답 작동 X
            console.log("상담 중에는 버튼식 자동응답이 비활성화됩니다.");
            return;
        }
		
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
function appendMessage(content, sender) {
    const div = document.getElementById('chatbot-content');
    if (!div) {
        console.error("chatbot-content 요소를 찾을 수 없습니다.");
        return;
    }
    const messageDiv = document.createElement('div');

    messageDiv.classList.add('message');
    if (sender === 'user') {
        messageDiv.classList.add('user');
    } else {
        messageDiv.classList.add('bot');
    }

    messageDiv.textContent = content;
    div.appendChild(messageDiv);
    div.scrollTop = div.scrollHeight;
}

// 입력창 활성화 함수 
function enableUserInput() {
    const userInput = document.getElementById('chatbot-user-input');
    const sendBtn = document.getElementById('chatbot-send-btn');
    
    userInput.disabled = false;
    userInput.placeholder = "상담사와 채팅을 시작하세요";
    sendBtn.disabled = false;
    console.log("입력창과 전송 버튼이 활성화되었습니다.");
}

// 입력창 비활성화 함수 (상담 종료 시)
function disableUserInput() {
    const userInput = document.getElementById('chatbot-user-input');
    const sendBtn = document.getElementById('chatbot-send-btn');

    userInput.disabled = true;
    sendBtn.disabled = true;
    userInput.placeholder = "현재는 버튼으로만 상담 가능합니다.";
}


