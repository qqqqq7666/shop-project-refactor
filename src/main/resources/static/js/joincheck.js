
document.getElementById('emailCheckBtn').addEventListener('click', function() {
	var email = document.getElementById('email').value;
	fetch('/check-email?email=' + email)
		.then(response => response.json())
		.then(data => {
			var emailErrorElement = document.getElementById('emailError');
			if (data.exists) {
                emailErrorElement.textContent = '이미 사용중인 이메일입니다.';
                emailErrorElement.style.color = 'red'; // 빨간색
            } 
			else {
                emailErrorElement.textContent = '사용할 수 있는 이메일입니다.';
                emailErrorElement.style.color = 'blue'; // 파란색
            }
		})
		.catch(error => console.error('Error', error));
});

document.getElementById('nicknameCheckBtn').addEventListener('click', function() {
    var nickname = document.getElementById('nickname').value;
    fetch('/check-nickname?nickname=' + nickname)
        .then(response => response.json())
        .then(data => {
			var emailErrorElement = document.getElementById('emailError');
			if (data.exists) {
                emailErrorElement.textContent = '이미 사용중인 닉네임입니다.';
                emailErrorElement.style.color = 'red'; // 빨간색
            } 
			else {
                emailErrorElement.textContent = '사용할 수 있는 닉네임입니다.';
                emailErrorElement.style.color = 'blue'; // 파란색
            }
        })
        .catch(error => console.error('Error:', error));
});