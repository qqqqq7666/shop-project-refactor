// 챗봇 위젯 토글 기능
document.getElementById('chatbot-header').addEventListener('click', function() {
    var widget = document.getElementById('chatbot-widget');
    if (widget.style.display === 'none' || widget.style.display === '') {
        widget.style.display = 'flex';
    } else {
        widget.style.display = 'none';
    }
});

// 메시지 전송 기능
document.getElementById('chatbot-send-btn').addEventListener('click', sendMessage);
document.getElementById('chatbot-user-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

// 사용자 메시지 및 답변 처리
function sendMessage() {
    var userInput = document.getElementById('chatbot-user-input').value.trim();
    if (userInput === "") return;

    appendMessage(userInput, 'user');
    document.getElementById('chatbot-user-input').value = '';

    fetch('/api/v1/chatbot', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'message=' + encodeURIComponent(userInput)
    })
    .then(response => response.json())
    .then(data => {
        appendMessage(data.response, 'bot');
        moveQuestionButtonsToBottom(); // 답변 후 질문 버튼 다시 아래로 이동
    })
    .catch(error => {
        appendMessage("에러가 발생했습니다.", 'bot');
        console.error('Error:', error);
        moveQuestionButtonsToBottom();
    });
}

// 추천 질문 버튼 클릭 이벤트
document.querySelectorAll('.chatbot-question-btn').forEach(button => {
    button.addEventListener('click', function() {
        const question = this.textContent;
        appendMessage(question, 'user');

        fetch('/api/v1/chatbot', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'message=' + encodeURIComponent(question)
        })
        .then(response => response.json())
        .then(data => {
            appendMessage(data.response, 'bot');
            moveQuestionButtonsToBottom(); // 답변 후 질문 버튼 다시 아래로 이동
        })
        .catch(error => {
            appendMessage("에러가 발생했습니다.", 'bot');
            console.error('Error:', error);
            moveQuestionButtonsToBottom();
        });
    });
});

// 메시지 추가 기능
function appendMessage(message, sender) {
    var chatbox = document.getElementById('chatbot-content');
    var messageDiv = document.createElement('div');
    messageDiv.className = 'message ' + sender;
    messageDiv.textContent = message;
    chatbox.appendChild(messageDiv);
    chatbox.scrollTop = chatbox.scrollHeight;
}

// 질문 버튼을 대화창 맨 아래로 이동
function moveQuestionButtonsToBottom() {
    const chatbox = document.getElementById('chatbot-content');
    const questionContainer = document.getElementById('chatbot-questions');
    
    if (questionContainer) {
        chatbox.appendChild(questionContainer); // 질문 버튼을 대화창 맨 아래로 이동
        chatbox.scrollTop = chatbox.scrollHeight; // 스크롤 맨 아래로 이동
    }
}
