// 카테고리 폼 제출
document.body.addEventListener('click', (event) => {
    if (event.target.matches('button[type="submit"]')) {
        // let form = event.target; // 클릭된 버튼

        // while (form && form.tagName !== 'FORM') {
        //     form = form.parentElement; // 부모 요소로 이동
        // }
        let form = event.target.closest('form');
        if (!form) return; // 폼이 없으면 종료
        console.log('Form ID:', form.id);

        // 브라우저 기본 검증 실행
        if (!form.checkValidity()) {
            form.reportValidity(); // 기본 브라우저 알림
            return;
        }
        event.preventDefault(); // 기본 제출 동작 중단

        const formData = new FormData(form);

        // 메인 카테고리 생성 폼인지 여부 추가
        let creatingMainCategory = form.id == 'main-category-form';

        // formData.append('jsonData', JSON.stringify(jsonData));
        formData.append('creatingMainCategory', creatingMainCategory);

        for (let pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }

        fetch('/categories/create', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 400) {  // badRequest
                    throw new Error('유효하지 않은 카테고리입니다(영대소문자, 한글, /만 가능).');
                } else if (response.status === 409) {  // Conflict
                    throw new Error('카테고리명이 중복되었습니다.');
                } else {
                    throw new Error('카테고리 추가에 실패했습니다.');
                }
            })
            .then(data => {


                console.log('Success:', data);
                alert('카테고리가 추가되었습니다.');
                location.reload(); // 페이지를 새로고침하여 목록 갱신
            })
            .catch(error => {
                console.error('Error:', error);
                alert('카테고리 추가에 실패했습니다.');
            });
    }
});


function toggleForm(formId) {
    const formContainer = document.getElementById(formId);
    formContainer.classList.toggle('active'); // 폼 활성화/비활성화
    formContainer.reset(); // 폼 초기화
}