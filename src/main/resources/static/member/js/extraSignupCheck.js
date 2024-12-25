// 닉네임 중복 검사
async function checkNickname() {
    const nicknameInput = document.getElementById('nickname');
    const nicknameErrorElement = document.getElementById('nicknameError');

    const nickname = nicknameInput.value.trim();
    if (!nickname) {
        nicknameErrorElement.textContent = '닉네임을 입력해주세요.';
        nicknameErrorElement.style.color = 'red';
        return;
    }

    try {
        const response = await fetch('/api/v1/members/check-nickname?nickname=' + encodeURIComponent(nickname));
        const data = await response.json();

        if (data.exists) {
            nicknameErrorElement.textContent = '이미 사용중인 닉네임입니다.';
            nicknameErrorElement.style.color = 'red';
        } else {
            nicknameErrorElement.textContent = '사용할 수 있는 닉네임입니다.';
            nicknameErrorElement.style.color = 'blue';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// 전화번호 자동 마스킹
function formatPhoneInput(phoneInput) {
    let value = phoneInput.value.replace(/[^0-9]/g, ''); // 숫자만 남기기
    if (value.length > 3 && value.length <= 7) {
        value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 7) {
        value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
    }
    phoneInput.value = value;
}

// DOMContentLoaded 시점에 이벤트 할당
document.addEventListener('DOMContentLoaded', function() {
    const nicknameCheckBtn = document.getElementById('nicknameCheckBtn');
    const phoneInput       = document.getElementById('phone');

    // 닉네임 중복 체크 버튼
    if (nicknameCheckBtn) {
        nicknameCheckBtn.addEventListener('click', checkNickname);
    }

    // 전화번호 마스킹
    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            formatPhoneInput(phoneInput);
        });
    }
});
