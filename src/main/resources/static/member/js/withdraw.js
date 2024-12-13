document.getElementById('withdrawBtn').addEventListener('click', function() {
	if (confirm("정말로 회원 탈퇴를 하시겠습니까? 탈퇴 시 15일동안 재가입이 불가하며 회원 정보는 15일 뒤 삭제됩니다")) {
        fetch('/mypage/withdraw', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('회원 탈퇴 처리 실패: ' + response.statusText);
            }
            return response.text();
        })
        .then(data => {
            alert(data);
            window.location.href = "/login"; // 로그아웃 후 우선은 로그인으로 리다이렉트
        })
        .catch(error => {
            alert(error.message); // 여기서 오류 메시지를 출력하도록 수정
        });
    }
});