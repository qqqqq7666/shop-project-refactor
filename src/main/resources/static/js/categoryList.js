// 메인 카테고리 폼 제출
document.querySelector('#main-category-form').addEventListener('submit', function (event) {
    event.preventDefault(); // 폼의 기본 제출 동작 중단

    const formData = new FormData(event.target); // 폼 데이터를 가져옴

    fetch('/categories/create', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('카테고리 생성 실패');
            }
            return response.json();
        })
        .then(data => {
            // 성공적으로 생성되었을 때
            console.log('Success:', data);
            alert('카테고리가 성공적으로 추가되었습니다!');
            location.reload(); // 페이지를 새로고침하여 목록 갱신
        })
        .catch(error => {
            // 에러 발생 시
            console.error('Error:', error);
            alert('카테고리 추가에 실패했습니다.');
        });
});

// 서브 카테고리 폼 제출
document.addEventListener('DOMContentLoaded', () => {
    // 서브 카테고리 폼 제출 이벤트 추가
    const subCategoryForms = document.querySelectorAll('[id^="sub-category-form-"]');
    subCategoryForms.forEach(form => {
        form.addEventListener('submit', (event) => {
            event.preventDefault(); // 폼의 기본 제출 동작 중단
            const formData = new FormData(form);

            fetch('/categories/create', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Failed to create subcategory');
                    }
                    return response.json();
                })
                .then(data => {
                    alert('서브 카테고리가 추가되었습니다.');
                    location.reload();
                    // 폼 초기화 및 숨김 처리
                    form.reset();
                    form.classList.remove('active');
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    });
});


function toggleForm(formId) {
    const formContainer = document.getElementById(formId);
    formContainer.classList.toggle('active'); // 폼 활성화/비활성화
}
function resetForm(formId) {
    document.getElementById(formId).reset(); // 폼 초기화
}