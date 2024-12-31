import { validatePassword, checkPasswordMatch, formatPhoneInput, validateForm } from './validationCheck.js';

let emailChecked = false;
let nicknameChecked = false;

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
		emailChecked = false;
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
		
        fetch('/api/v1/members/check-email?email=' + email)
		.then(response => {
            if (response.status === 409) {
                emailErrorElement.textContent = '이미 사용중인 이메일입니다.';
                emailErrorElement.style.color = 'red';
            } else if (response.ok) {
                return response.json();
            } else {
                throw new Error('Unexpected response status');
            }
        })
        .then(data => {
            if (data && !data.exists) {
                emailErrorElement.textContent = '사용할 수 있는 이메일입니다.';
                emailErrorElement.style.color = 'blue';
				emailChecked = true;
            }
        })
        .catch(error => console.error('Error', error));
    });

    // 닉네임 중복 확인
    document.getElementById('nicknameCheckBtn').addEventListener('click', function() {
        const nickname = nicknameInput.value;
		const nicknameErrorElement = document.getElementById('nicknameError');
		nicknameChecked = false;
		if (!nickname) {
	        nicknameErrorElement.textContent = '닉네임을 입력해주세요.';
	        nicknameErrorElement.style.color = 'red';
	        return;
	    }
		
        fetch('/api/v1/members/check-nickname?nickname=' + nickname)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                nicknameErrorElement.textContent = '이미 사용중인 닉네임입니다.';
                nicknameErrorElement.style.color = 'red';
            } else {
                nicknameErrorElement.textContent = '사용할 수 있는 닉네임입니다.';
                nicknameErrorElement.style.color = 'blue';
				nicknameChecked = true;
            }
        })
        .catch(error => console.error('Error:', error));
    });

	// 비밀번호 유효성 검사
    passwordInput.addEventListener('input', function () {
        const errorMessage = validatePassword(passwordInput.value);
        if (errorMessage) {
            passwordError.textContent = errorMessage;
            passwordError.style.color = 'red';
        } else {
            passwordError.textContent = '올바른 패스워드 양식입니다.';
            passwordError.style.color = 'blue';
        }
    });

	// 비밀번호 일치 확인
    function checkPasswords() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // 비밀번호 확인 칸이 비어있으면 메시지 초기화
        if (!confirmPassword.trim()) {
            passwordConfirmError.textContent = '';
            return;
        }

        const message = checkPasswordMatch(password, confirmPassword);
        if (message === '비밀번호가 일치하지 않습니다.') {
            passwordConfirmError.textContent = message;
            passwordConfirmError.style.color = 'red';
        } else {
            passwordConfirmError.textContent = message;
            passwordConfirmError.style.color = 'blue';
        }
    }

    // 비밀번호와 확인 비밀번호에 각각 이벤트 리스너 등록
    passwordInput.addEventListener('input', checkPasswords);
    confirmPasswordInput.addEventListener('input', checkPasswords);

    // 실시간 전화번호 입력 마스크
    phoneInput.addEventListener('input', function () {
        formatPhoneInput(phoneInput);
    });

    // 폼 전체 유효성 검사
    form.addEventListener('submit', function (e) {
        const fields = [
            { element: passwordInput, errorId: 'passwordError', message: '비밀번호를 입력해주세요.' },
            { element: phoneInput, errorId: 'phoneError', message: '전화번호를 입력해주세요.' },
        ];
        const isValid = validateForm(fields);
		
		if (!emailChecked|| !nicknameChecked) {
		    e.preventDefault();
		    alert('이메일/닉네임 중복확인을 해주세요.');
		    return;
		}
		
        if (!isValid) {
            e.preventDefault(); // 폼 제출 방지
        }
    });
});