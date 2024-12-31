
let stompClient = null;
let currentRoomId = null;
let userId = "USER";  // 실제 로그인된 사용자 ID
const CHAT_STORAGE_KEY = 'chatbotMessages';

// 상담사 연결 버튼
document.getElementById('connect-admin-btn').addEventListener('click', function() {
	
	if (window.isCounseling) {
	    console.log("이미 상담 중입니다.");
	    return;
	}
	
    fetch('/api/v1/chat/connection', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userId)
    })
    .then(res => res.json())
    .then(chatRoom => {
        currentRoomId = chatRoom.roomId;
        // 1) 여기서는 "방 생성 완료(대기중)" 안내만
        userappendMessage("상담 요청이 접수되었습니다. 상담사가 연결할 때까지 대기 바랍니다.", 'bot');

        // 2) 주기적으로 방 상태 체크
        checkRoomStatusPeriodically();
    })
    .catch(err => {
        console.error(err);
        userappendMessage("상담사 연결에 실패했습니다.", 'bot');
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
                  startCounseling();
              }
          })
          .catch(err => console.error(err));
    }, 3000);
}

// 상담 시작
function startCounseling() {
    window.isCounseling = true;      // 상담 모드 진입
    connectWebSocket();       // WebSocket 연결
    enableUserInput();        // 입력창 활성화(챗봇)
    userappendMessage("상담사 연결 성공! 메시지를 입력하세요.", 'bot');
}

// WebSocket 연결
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function(message) {
            const msgBody = JSON.parse(message.body);

			if (msgBody.roomId === currentRoomId) {
			    console.log('RoomID 일치함, appendMessage 호출!');
			    userappendMessage(msgBody.content, msgBody.sender);
			} else {
			    console.warn('RoomID 불일치: ', msgBody.roomId, currentRoomId);
			}
        });

        // (추가) 상담 종료 구독 
        stompClient.subscribe('/topic/chat/end', function(message) {
            const msgBody = JSON.parse(message.body);
            // 방 ID가 내 currentRoomId와 같다면 상담 종료
            if (msgBody.roomId === currentRoomId) {
                userappendMessage("상담이 종료되었습니다.", 'bot');
                endCounseling();  // 상담 종료
            }
        });
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
    // 상담 중일 때만 메시지 전송
    if (!window.isCounseling) {
        console.warn("버튼식 상담만 가능합니다. (현재 상담 모드가 아님)");
        return;
    }

    const msgInput = document.getElementById('chatbot-user-input');
    const content = msgInput.value.trim();
    if (!content || !stompClient) return;

    stompClient.send("/app/chat/send", {}, JSON.stringify({
        roomId: currentRoomId,
        sender: userId,
        content: content
    }));
    msgInput.value = "";
}

function userappendMessage(content, sender) {
    const container = document.getElementById('chatbot-content');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message');

    if (sender === userId) {
        messageDiv.classList.add('user');  // 유저: 오른쪽 파란색
    } else {
        messageDiv.classList.add('bot');   // 어드민: 왼쪽 초록색
    }

    messageDiv.textContent = content;
    container.appendChild(messageDiv);
    container.scrollTop = container.scrollHeight;
}

// 상담 종료 시 처리
function endCounseling() {
    // 상담 중단 플래그
    isCounseling = false;
    currentRoomId = null;

    // 입력창 비활성화
    disableUserInput();

	if (stompClient) {
	    stompClient.disconnect(() => {
	        console.log("WebSocket 연결이 종료되었습니다.");
	    });
	}
}