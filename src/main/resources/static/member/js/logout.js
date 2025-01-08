async function logout() {
    try {
        const response = await fetch('/jwt-logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
			localStorage.removeItem('chatbotMessages');
			localStorage.removeItem('currentRoomId');
			localStorage.removeItem('isCounseling');
			localStorage.removeItem('userChatMessages');
            alert('로그아웃 되었습니다.');
            window.location.href = '/login';
        } else {
            alert('로그아웃 실패. 다시 시도해주세요.');
        }
    } catch (error) {
        console.error('로그아웃 중 오류 발생:', error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}