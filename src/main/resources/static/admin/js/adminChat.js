let stompClient = null;
let adminId = "ADMIN_001";   // 실제 로그인 관리자 ID
let selectedRoomId = null;

function joinRoom(roomId) {
    // 방 상태를 IN_PROGRESS로 업데이트
    fetch('/api/v1/chat/admin/assign?roomId=' + roomId + '&adminId=' + adminId)
    .then(() => {
        alert("해당 방 승인 완료 (IN_PROGRESS)");
        selectedRoomId = roomId;
        document.getElementById('room-id-span').innerText = roomId;
        document.getElementById('chat-section').style.display = 'block';

        // WebSocket 연결
        connectStomp();
    })
    .catch(err => console.error(err));
}

function connectStomp() {
    if (stompClient && stompClient.connected) {
        return;
    }
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log("Connected: " + frame);
        // 구독
        stompClient.subscribe('/topic/messages', function(message) {
            const msgBody = JSON.parse(message.body);
            if (msgBody.roomId === selectedRoomId) {
                appendMessage(msgBody.content, msgBody.sender);
            }
        });
    });
}

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

function appendMessage(content, sender) {
    const box = document.getElementById('chat-content');
    const p = document.createElement('p');
    p.textContent = sender + ": " + content;
    box.appendChild(p);
    box.scrollTop = box.scrollHeight;
}