
document.getElementById('emailCheckBtn').addEventListener('click', function() {
	var email = document.getElementById('email').value;
	fetch('/check-email?email=' + email)
		.then(response => response.json())
		.then(data => {
			if (data.exists) {
				document.getElementById('emailError').textContent = '이미 사용중인 이메일입니다.';
			}
			else {
				document.getElementById('emailError').textContent = '';
			}
		})
		.catch(error => console.error('Error', error));
});

document.getElementById('nicknameCheckBtn').addEventListener('click', function() {
    var nickname = document.getElementById('nickname').value;
    fetch('/check-nickname?nickname=' + nickname)
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                document.getElementById('nicknameError').textContent = '이미 사용 중인 닉네임입니다.';
            } else {
                document.getElementById('nicknameError').textContent = '';
            }
        })
        .catch(error => console.error('Error:', error));
});