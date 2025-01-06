let stompClient = null;
let adminId = "ADMIN";   // 실제 로그인 관리자 ID
let selectedRoomId = null;

function joinRoom(roomId) {
    // 상담사 할당
    fetch('/api/v1/chat/admin/assign?roomId=' + roomId + '&adminId=' + adminId)
    .then(() => {
        alert("승인 완료 (IN_PROGRESS)");
        selectedRoomId = roomId;
        document.getElementById('room-id-span').innerText = roomId;
        document.getElementById('chat-section').style.display = 'block';

        connectStomp();
    })
    .catch(err => console.error(err));
}

function connectStomp() {
    if (stompClient && stompClient.connected) return;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log("Connected: " + frame);

        // 메시지 수신
        stompClient.subscribe('/topic/messages', function(message) {
            const msgBody = JSON.parse(message.body);
            if (msgBody.roomId === selectedRoomId) {
                appendMessage(msgBody.content, msgBody.sender);
            }
        });
		
		// 상담 종료 알림 수신
		stompClient.subscribe('/topic/chat/end', function(message) {
		    const msgBody = JSON.parse(message.body);
		    if (msgBody.roomId === selectedRoomId) {
		        alert("상담이 종료되었습니다.");
		        closeChatSession();
		    }
		});
    });
}

// 관리자 메시지 전송
function sendAdminMsg() {
    const input = document.getElementById('adminMsg');
    const content = input.value.trim();
    if (!content) return;

    stompClient.send("/app/chat/send", {}, JSON.stringify({
        roomId: selectedRoomId,
        sender: adminId,
        content: content
    }));
    input.value = "";
}

// 상담 종료 로직
function endChat() {
    if (!selectedRoomId) {
        alert("연결된 방이 없습니다.");
        return;
    }

    // 서버에 상태 변경 요청 (방 종료)
    fetch('/api/v1/chat/admin/end?roomId=' + selectedRoomId, {
        method: 'POST'
    })
    .then(() => {
        alert("상담 종료 요청 완료");
        // 서버가 종료 처리 후, 사용자에게도 알림(WebSocket or REST)
        // 아래 코드는 서버가 end 이벤트를 브로드캐스트한다고 가정:
        stompClient.send("/app/chat/end", {}, JSON.stringify({
            roomId: selectedRoomId,
            sender: adminId
        }));
        
		// 상담 종료 후 UI 초기화
		closeChatSession();
    })
    .catch(err => console.error(err));
}

// 상담 종료 시 화면 초기화 함수
function closeChatSession() {
    document.getElementById('chat-section').style.display = 'none';
    document.getElementById('room-id-span').innerText = "";
    selectedRoomId = null;
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log("WebSocket 연결이 종료되었습니다.");
        });
    }
}

// 채팅 UI에 "상담 종료" 버튼 추가
(function() {
    const chatSection = document.getElementById('chat-section');
    const endButton = document.createElement('button');
    endButton.innerText = "상담 종료";
    endButton.onclick = endChat;
    chatSection.appendChild(endButton);
})();


function appendMessage(content, sender) {
	console.log("내가불리나?");
	const container = document.getElementById('chatbot-content');

	if (!container) {
	    console.error("chatbot-content 요소를 찾을 수 없습니다.");
	    return;
	}

	const messageDiv = document.createElement('div');
	messageDiv.classList.add('message');

	if (sender === adminId) {
	    messageDiv.classList.add('bot'); // 어드민 메시지 (왼쪽)
		messageDiv.textContent = "상담사: ";
	} else {
	    messageDiv.classList.add('user'); // 사용자 메시지 (오른쪽)
		messageDiv.textContent = "고객: ";
	}
	
	messageDiv.textContent += content;
	container.appendChild(messageDiv);
	container.scrollTop = container.scrollHeight;
}

