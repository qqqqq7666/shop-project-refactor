document.addEventListener('DOMContentLoaded', function() {
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const phoneInput = document.getElementById('phone');
    const postNoInput = document.getElementById('postNo');
    const form = document.querySelector('form');

    // 이메일 중복 확인
    document.getElementById('emailCheckBtn').addEventListener('click', function() {
        const email = emailInput.value;
		const emailErrorElement = document.getElementById('emailError');
		
		if (!email) {
	        emailErrorElement.textContent = '이메일을 입력해주세요.';
	        emailErrorElement.style.color = 'red';
	        return;
		}
		
		const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	    if (!emailRegex.test(email)) {
	        emailErrorElement.textContent = '올바른 이메일 형식이 아닙니다.';
	        emailErrorElement.style.color = 'red';
	        return;
	    }
		
        fetch('/api/member/check-email?email=' + email)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                emailErrorElement.textContent = '이미 사용중인 이메일입니다.';
                emailErrorElement.style.color = 'red';
            } else {
                emailErrorElement.textContent = '사용할 수 있는 이메일입니다.';
                emailErrorElement.style.color = 'blue';
            }
        })
        .catch(error => console.error('Error', error));
    });

    // 닉네임 중복 확인
    document.getElementById('nicknameCheckBtn').addEventListener('click', function() {
        const nickname = nicknameInput.value;
		const nicknameErrorElement = document.getElementById('nicknameError');
		
		if (!nickname) {
	        nicknameErrorElement.textContent = '닉네임을 입력해주세요.';
	        nicknameErrorElement.style.color = 'red';
	        return;
	    }
		
        fetch('/api/member/check-nickname?nickname=' + nickname)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                nicknameErrorElement.textContent = '이미 사용중인 닉네임입니다.';
                nicknameErrorElement.style.color = 'red';
            } else {
                nicknameErrorElement.textContent = '사용할 수 있는 닉네임입니다.';
                nicknameErrorElement.style.color = 'blue';
            }
        })
        .catch(error => console.error('Error:', error));
    });

    // 비밀번호 유효성 검사
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        const passwordError = document.getElementById('passwordError');
        const regex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        if (!regex.test(password)) {
            passwordError.textContent = '비밀번호는 최소 8자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.';
            passwordError.style.color = 'red';
        } else {
            passwordError.textContent = '올바른 패스워드 양식입니다';
            passwordError.style.color = 'blue';
        }
    });

    // 비밀번호 일치 확인
    confirmPasswordInput.addEventListener('input', function() {
        const password = passwordInput.value;
        const confirmPassword = this.value;
        const passwordConfirmError = document.getElementById('passwordConfirmError');

        if (password !== confirmPassword) {
            passwordConfirmError.textContent = '비밀번호가 일치하지 않습니다.';
            passwordConfirmError.style.color = 'red';
        } else {
            passwordConfirmError.textContent = '비밀번호가 일치합니다.';
            passwordConfirmError.style.color = 'blue';
        }
    });

    // 폼 제출 시 전체 유효성 검사
    form.addEventListener('submit', function(e) {
        let isValid = true;
        const fields = [
            { element: emailInput, errorId: 'emailError', message: '이메일을 입력해주세요.' },
            { element: nicknameInput, errorId: 'nicknameError', message: '닉네임을 입력해주세요.' },
            { element: passwordInput, errorId: 'passwordError', message: '비밀번호를 입력해주세요.' },
            { element: phoneInput, errorId: 'phoneError', message: '전화번호를 입력해주세요.' },
            { element: postNoInput, errorId: 'postNoError', message: '우편번호를 검색해주세요.' }
        ];

        fields.forEach(field => {
            const errorElement = document.getElementById(field.errorId) || document.createElement('span');
            
            if (!field.element.value.trim()) {
                errorElement.textContent = field.message;
                errorElement.style.color = 'red';
                field.element.parentNode.insertBefore(errorElement, field.element.nextSibling);
                isValid = false;
            } else {
                if (errorElement.textContent) {
                    errorElement.textContent = '';
                }
            }
        });

        // 비밀번호 확인 추가 검증
        const passwordConfirmError = document.getElementById('passwordConfirmError');
        if (passwordInput.value !== confirmPasswordInput.value) {
            passwordConfirmError.textContent = '비밀번호가 일치하지 않습니다.';
            passwordConfirmError.style.color = 'red';
            isValid = false;
        } else {
            passwordConfirmError.textContent = '';
        }

        // 비밀번호 형식 검증
        const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        const passwordError = document.getElementById('passwordError');
        if (!passwordRegex.test(passwordInput.value)) {
            passwordError.textContent = '비밀번호는 최소 8자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.';
            passwordError.style.color = 'red';
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault(); // 폼 제출 방지
        }
    });
});