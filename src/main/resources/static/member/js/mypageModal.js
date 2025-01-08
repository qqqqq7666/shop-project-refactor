document.addEventListener('DOMContentLoaded', function () {
    const editProfileBtn = document.getElementById('edit-profile-btn');
    const passwordModal = document.getElementById('password-modal');
    const passwordInput = document.getElementById('password-input');
    const confirmBtn = document.getElementById('password-confirm-btn');
    const cancelBtn = document.getElementById('password-cancel-btn');

    // 모달 열기
    editProfileBtn.addEventListener('click', function () {
        passwordModal.style.display = 'flex';
        passwordInput.value = '';
        passwordInput.focus();
    });

    // 모달 닫기
    cancelBtn.addEventListener('click', function () {
        passwordModal.style.display = 'none';
    });

    // 비밀번호 검증
    confirmBtn.addEventListener('click', function () {
        const password = passwordInput.value.trim();
        if (!password) {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        fetch('/api/v1/members/verify-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ password })
        })
        .then(response => {
            if (response.ok) {
                // 비밀번호 확인 성공 시 이동
                window.location.href = '/mypage/edit';
            } else {
                // 비밀번호 확인 실패 시
                alert('비밀번호가 일치하지 않습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        });
    });
});
