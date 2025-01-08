let isCounseling = false;
let stompClient = null;
let currentRoomId = null;
let userId = "USER";  // 실제 로그인된 사용자 ID
const CHAT_STORAGE_KEY = 'chatbotMessages';

// 페이지가 완전히 로드된 시점에 이전 상태 복구를 시도

window.onload = function () {
    loadChatState();
};

// 상담사 연결 버튼
document.getElementById('connect-admin-btn').addEventListener('click', function() {

    // (기존 로직) 상담방 생성
    fetch('/api/v1/chat/connection', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userId)
    })
    .then(res => res.json())
    .then(chatRoom => {
        currentRoomId = chatRoom.roomId;
        userappendMessage("상담 요청이 접수되었습니다. 상담사가 연결할 때까지 대기 바랍니다.", 'bot');

        // 상태 저장
        saveChatState();

        // 방 상태 주기 체크
        checkRoomStatusPeriodically();
    })
    .catch(err => {
        console.error(err);
        userappendMessage("상담사 연결에 실패했습니다.", 'bot');
    });
});

// 방 상태 폴링
function checkRoomStatusPeriodically() {
    const intervalId = setInterval(() => {
        fetch(`/api/v1/chat/room-status?roomId=${currentRoomId}`)
          .then(res => res.json())
          .then(room => {
              if (room.status === 'IN_PROGRESS') {
                  clearInterval(intervalId);
                  startCounseling();  // 상담 시작
              }
          })
          .catch(err => console.error(err));
    }, 3000);
}

// 상담 시작
function startCounseling() {
    isCounseling = true;
    connectWebSocket();
    enableUserInput();
	
	/*if (typeof hideQuestionButtons === 'function') {
	    hideQuestionButtons(); // 버튼 숨기기 + 콘텐츠 확장
	}*/
	
    userappendMessage("상담사 연결 성공! 메시지를 입력하세요.", 'bot');

    // (추가) 상태 저장
    saveChatState();
}

// WebSocket 연결
function connectWebSocket() {
    if (!currentRoomId) return;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function(message) {
            const msgBody = JSON.parse(message.body);
            if (msgBody.roomId === currentRoomId) {
                userappendMessage(msgBody.content, msgBody.sender);
            }
        });

        // 상담 종료 수신
        stompClient.subscribe('/topic/chat/end', function(message) {
            const msgBody = JSON.parse(message.body);
            if (msgBody.roomId === currentRoomId) {
                userappendMessage("상담이 종료되었습니다.", 'bot');
                endCounseling();
            }
        });
    });
}

// 전송 버튼
document.getElementById('chatbot-send-btn').addEventListener('click', function() {
    sendMessage();
});

// 엔터키 전송
document.getElementById('chatbot-user-input').addEventListener('keypress', function(e) {
    if(e.key === 'Enter') {
        sendMessage();
    }
});

function sendMessage() {
    if (!isCounseling) {
        console.warn("현재 상담 모드가 아닙니다. 버튼식 상담만 이용 가능합니다.");
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

// 메시지 화면 출력
function userappendMessage(content, sender) {
    const container = document.getElementById('chatbot-content');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message');

    if (sender === userId) {
        messageDiv.classList.add('user');  // 오른쪽
    } else {
        messageDiv.classList.add('bot');   // 왼쪽
    }

    messageDiv.textContent = content;
    container.appendChild(messageDiv);
    container.scrollTop = container.scrollHeight;

    // (추가) 메시지를 localStorage에 저장
    storeChatMessage(content, sender);
}

// 상담 종료 시
function endCounseling() {
    isCounseling = false;
    currentRoomId = null;
    disableUserInput();

	/*if (typeof showQuestionButtons === 'function') {
	    showQuestionButtons(); // 버튼 다시 표시 + 콘텐츠 복구
	}*/
	
    // WebSocket 연결 해제
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log("WebSocket 연결이 종료되었습니다.");
        });
    }
    // (추가) localStorage 정리
    clearChatState();

}

/* ========== 여기서부터는 추가된 부분(상태 저장/복구 로직) ========== */

const MAX_CHAT_MESSAGES = 5;
// 메시지를 localStorage에 기록 
function storeChatMessage(content, sender) {
    // 기존 배열 불러오기
    let chatLog = localStorage.getItem('userChatMessages');
    let messages = chatLog ? JSON.parse(chatLog) : [];

    // 새 메시지 추가
    messages.push({
        content,
        sender
    });
	
	if (messages.length > MAX_CHAT_MESSAGES) {
        // 가장 오래된 메시지 제거
        messages = messages.slice(-MAX_CHAT_MESSAGES);
        console.log(`오래된 메시지 정리 완료: ${messages.length}개의 메시지가 유지됩니다.`);
    }

    // 다시 저장
    localStorage.setItem('userChatMessages', JSON.stringify(messages));
    // 상담 상태도 갱신
    saveChatState();
}

/** 상담 상태(currentRoomId, isCounseling)를 localStorage에 저장 */
function saveChatState() {
    localStorage.setItem('currentRoomId', currentRoomId || '');
    localStorage.setItem('isCounseling', isCounseling ? 'true' : 'false');
}

/** 페이지 로드 시 상담 상태가 '진행 중'이면 대화 복구 + WebSocket 재연결 */
function loadChatState() {
    const storedRoomId = localStorage.getItem('currentRoomId');
    const storedIsCounseling = localStorage.getItem('isCounseling');

    // 로컬스토리지에 상담 중이라고 저장되어 있으면
    if (storedIsCounseling === 'true' && storedRoomId) {
        currentRoomId = storedRoomId;
        isCounseling = true;

        // 1) 기존 대화 로그 복구
        let chatLog = localStorage.getItem('userChatMessages');
        if (chatLog) {
            JSON.parse(chatLog).forEach(msg => {
                userappendMessage(msg.content, msg.sender);
            });
        }
        // 2) WebSocket 재연결 및 입력창 활성화
        connectWebSocket();
        enableUserInput();
    } else {
        // 상담 진행 중이 아니면 입력창 비활성화 상태로 남겨둔다
        disableUserInput();
    }
}

/** 상담 완전히 종료 시 localStorage 정리 */
function clearChatState() {
    localStorage.removeItem('currentRoomId');
    localStorage.removeItem('isCounseling');
    localStorage.removeItem('userChatMessages');
}
