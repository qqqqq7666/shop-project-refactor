// 비밀번호 유효성 검사 함수
export function validatePassword(password) {
    const regex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!regex.test(password)) {
        return '비밀번호는 최소 8글자이며 대소문자, 숫자, 특수문자(@$!%*?&)를 최소 하나씩 포함해야 합니다';
    }
    return '';
}

// 비밀번호 일치 확인 함수
export function checkPasswordMatch(password, confirmPassword) {
    if (password !== confirmPassword) {
        return '비밀번호가 일치하지 않습니다.';
    }
    return '비밀번호가 일치합니다.';
}

// 실시간 전화번호 입력 마스크
export function formatPhoneInput(phoneInput) {
    let value = phoneInput.value.replace(/[^0-9]/g, ''); // 숫자만 남기기
    if (value.length > 3 && value.length <= 7) {
        value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 7) {
        value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
    }
    phoneInput.value = value;
}

// 폼 전체 유효성 검사 함수
export function validateForm(fields) {
    let isValid = true;
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
    return isValid;
}
