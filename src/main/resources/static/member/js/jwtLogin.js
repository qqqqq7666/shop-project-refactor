// 로그인 기능 처리

document.getElementById('loginButton').addEventListener('click', async () => {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!email || !password) {
        alert('이메일과 비밀번호를 입력해주세요.');
        return;
    }

    try {
        const response = await fetch('/jwt-login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            const accessToken = data.accessToken;

            // Access Token 저장
            localStorage.setItem('accessToken', accessToken);

            // 메인 페이지로 이동
            window.location.href = '/';
        } else {
            const errorMessage = await response.text();
            alert('로그인에 실패했습니다');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('로그인 중 오류가 발생했습니다.');
    }
});
