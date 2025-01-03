// let isCounseling = false;

window.onload = function () {
    loadChatbotMessages();
	cleanChatbotMessages();
};

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

	    // (추가) 메시지를 localStorage에도 저장
	    saveChatbotMessages(content, sender);
}

/* 메시지를 localStorage에 저장 */
function saveChatbotMessages(content, sender) {
    // 기존에 저장된 챗봇 대화 배열을 불러옴
    let storedData = localStorage.getItem('chatbotMessages');
    let messages = storedData ? JSON.parse(storedData) : [];

    // 새 메시지 추가
    messages.push({ content, sender });
	
	// 예) 최대 50개까지만 저장
	    const maxMessages = 10;
	    if (messages.length > maxMessages) {
	        messages = messages.slice(-maxMessages); 
	        // 가장 최근 50개의 메시지만 남기고 나머지는 버림
	    }

    // 다시 localStorage에 저장
    localStorage.setItem('chatbotMessages', JSON.stringify(messages));
}

/* 페이지 로드시 localStorage에 있는 챗봇 대화 복구 */
function loadChatbotMessages() {
    let storedData = localStorage.getItem('chatbotMessages');
    if (!storedData) return; // 저장된 기록이 없으면 종료

    let messages = JSON.parse(storedData);
    messages.forEach(msg => {
        appendMessage(msg.content, msg.sender);
    });
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

function cleanChatbotMessages(maxMessages = 10) {
	console.log('cleanChatbotMessages 함수가 호출되었습니다.');
	
    let storedData = localStorage.getItem('chatbotMessages');
    if (!storedData) return;

    try {
        let messages = JSON.parse(storedData);
        
        // 최대 개수를 초과하는 경우 오래된 메시지 삭제
        if (messages.length > maxMessages) {
            console.warn(`chatbotMessages가 ${maxMessages}개를 초과하여 오래된 메시지를 삭제합니다.`);
            messages = messages.slice(-maxMessages); // 최신 maxMessages만 유지
            localStorage.setItem('chatbotMessages', JSON.stringify(messages));
        }
    } catch (error) {
        console.error('로컬 스토리지 정리 중 오류 발생:', error);
        localStorage.removeItem('chatbotMessages'); // 오류 발생 시 데이터 초기화
    }
}

// 상담 시작 시 버튼 상담 숨기기
function hideQuestionButtons() {
	const questionSection = document.getElementById('chatbot-questions');
	const contentSection = document.getElementById('chatbot-content');

	if (questionSection && contentSection) {
	    questionSection.classList.add('hidden');
		contentSection.style.flex = '1 1 auto'; // 남은 공간 채우기
	    console.log("버튼 영역이 숨겨졌습니다.");
	} else {
	    console.warn("chatbot-questions 또는 chatbot-content 요소를 찾을 수 없습니다.");
	}
}

// 상담 종료 시 버튼 상담 보이기
function showQuestionButtons() {
	const questionSection = document.getElementById('chatbot-questions');
	const contentSection = document.getElementById('chatbot-content');

	if (questionSection && contentSection) {
	    questionSection.classList.remove('hidden');
	    console.log("버튼 영역이 다시 표시되었습니다.");
	} else {
	    console.warn("chatbot-questions 또는 chatbot-content 요소를 찾을 수 없습니다.");
	}
}

