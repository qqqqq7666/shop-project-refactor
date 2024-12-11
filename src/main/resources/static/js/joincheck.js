
document.getElementById('emailCheckBtn').addEventListener('click', function() {
	var email = document.getElementById('email').value;
	fetch('/api/member/check-email?email=' + email)
		.then(response => response.json())
		.then(data => {
			var emailErrorElement = document.getElementById('emailError');
			if (data.exists) {
                emailErrorElement.textContent = '이미 사용중인 이메일입니다.';
                emailErrorElement.style.color = 'red'; 
            } 
			else {
                emailErrorElement.textContent = '사용할 수 있는 이메일입니다.';
                emailErrorElement.style.color = 'blue'; 
            }
		})
		.catch(error => console.error('Error', error));
});

document.getElementById('nicknameCheckBtn').addEventListener('click', function() {
    var nickname = document.getElementById('nickname').value;
    fetch('/api/member/check-nickname?nickname=' + nickname)
        .then(response => response.json())
        .then(data => {
			var emailErrorElement = document.getElementById('nicknameError');
			if (data.exists) {
                emailErrorElement.textContent = '이미 사용중인 닉네임입니다.';
                emailErrorElement.style.color = 'red'; 
            } 
			else {
                emailErrorElement.textContent = '사용할 수 있는 닉네임입니다.';
                emailErrorElement.style.color = 'blue'; 
            }
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('password').addEventListener('input', function() {
    var password = this.value;
    var passwordError = document.getElementById('passwordError');

    var regex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    if (!regex.test(password)) {
        passwordError.textContent = '비밀번호는 최소 8자, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.';
		passwordError.style.color = 'red'; 
    } else {
        passwordError.textContent = '올바른 패스워드 양식입니다';
		passwordError.style.color = 'blue'; 
    }
});

document.getElementById('confirmPassword').addEventListener('input', function() {
    var password = document.getElementById('password').value;
    var confirmPassword = this.value;
    var passwordConfirmError = document.getElementById('passwordConfirmError');

    if (password !== confirmPassword) {
        passwordConfirmError.textContent = '비밀번호가 일치하지 않습니다.';
        passwordConfirmError.style.color = 'red';
    } else {
        passwordConfirmError.textContent = '비밀번호가 일치합니다.';
        passwordConfirmError.style.color = 'blue';
    }
});