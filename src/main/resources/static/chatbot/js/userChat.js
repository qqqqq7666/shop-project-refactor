let stompClient = null;
let currentRoomId = null;
let userId = "USER_123";  // 실제 로그인된 사용자 ID

// 상담사 연결 버튼
document.getElementById('connect-admin-btn').addEventListener('click', function() {
    fetch('/api/v1/chat/connection', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userId)
    })
    .then(res => res.json())
    .then(chatRoom => {
        currentRoomId = chatRoom.roomId;
        // 1) 여기서는 "방 생성 완료(대기중)" 안내만
        appendMessage("상담 요청이 접수되었습니다. 상담사가 연결할 때까지 대기...", 'bot');

        // 2) 주기적으로 방 상태 체크
        checkRoomStatusPeriodically();
    })
    .catch(err => {
        console.error(err);
        appendMessage("상담사 연결에 실패했습니다.", 'bot');
    });
});

// 방 상태 폴링
function checkRoomStatusPeriodically() {
    // 3초 간격으로 방 상태 조회
    const intervalId = setInterval(() => {
        fetch(`/api/v1/chat/room-status?roomId=${currentRoomId}`)
          .then(res => res.json())
          .then(room => {
              if (room.status === 'IN_PROGRESS') {
                  // Admin이 승인(방 상태 변경)하면, 그때 connectWebSocket
                  clearInterval(intervalId); 
                  connectWebSocket();
              }
          })
          .catch(err => console.error(err));
    }, 3000);
}

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function(message) {
            const msgBody = JSON.parse(message.body);
            if (msgBody.roomId === currentRoomId) {
                appendMessage(msgBody.content, msgBody.sender);
            }
        });
        
        enableUserInput();
        appendMessage("상담사 연결 성공! 메시지를 입력해주세요.", 'bot');
    });
}

// 전송 버튼 클릭
document.getElementById('chatbot-send-btn').addEventListener('click', function() {
    sendMessage();
});

// 엔터키로 메시지 전송
document.getElementById('chatbot-user-input').addEventListener('keypress', function(e) {
    if(e.key === 'Enter') {
        sendMessage();
    }
});

function sendMessage() {
    const msgInput = document.getElementById('chatbot-user-input');
    const content = msgInput.value.trim();
    if (!content || !stompClient) return;

    // WebSocket 전송
    stompClient.send("/app/chat/send", {}, JSON.stringify({
        roomId: currentRoomId,
        sender: userId,      // 실제 userId
        content: content
    }));
    msgInput.value = "";
}

function appendMessage(content, sender) {
    const div = document.getElementById('chatbot-content');
    const p = document.createElement('p');
    p.textContent = sender + ": " + content;
    div.appendChild(p);
    div.scrollTop = div.scrollHeight;
}

// 이미 chatbotWork.js에 있는 함수
function enableUserInput() {
    const userInput = document.getElementById('chatbot-user-input');
    const sendBtn = document.getElementById('chatbot-send-btn');
    
    userInput.disabled = false;
    userInput.placeholder = "상담사와 채팅을 시작하세요";
    sendBtn.disabled = false;
    console.log("입력창과 전송 버튼이 활성화되었습니다.");
}