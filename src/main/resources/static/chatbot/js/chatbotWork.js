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
    if(e.key === 'Enter') {
        sendMessage();
    }
});

function sendMessage() {
    var userInput = document.getElementById('chatbot-user-input').value.trim();
    if(userInput === "") return;

    appendMessage(userInput, 'user');
    document.getElementById('chatbot-user-input').value = '';

    fetch('/api/v1/chatbot', { // 서블릿 URL 패턴
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'message=' + encodeURIComponent(userInput)
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

function appendMessage(message, sender) {
    var chatbox = document.getElementById('chatbot-content');
    var messageDiv = document.createElement('div');
    messageDiv.className = 'message ' + sender;
    messageDiv.textContent = message;
    chatbox.appendChild(messageDiv);
    chatbox.scrollTop = chatbox.scrollHeight;
}