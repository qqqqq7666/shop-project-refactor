// 수정 버튼 클릭 시
function editCategory(tagId, categoryId) {
    console.log("#1 categoryId: " + categoryId);
    // 기존 요소와 부모 요소 가져오기
    const categoryElement = document.getElementById(tagId);
    const currentText = categoryElement.textContent;
    const parentElement = categoryElement.parentElement;

    // 버튼 컨테이너 가져오기
    const btnContainer = document.getElementById('btn-container-' + categoryId);

    // 기존 요소의 텍스트와 수정, 삭제, 추가 버튼 숨기기
    categoryElement.style.display = 'none';
    const editButtons = btnContainer.querySelectorAll('.update-btn, .delete-btn, .add-sub-category-btn');
    for (let i=0 ; i<editButtons.length ; i++) {
        if (editButtons[i]) editButtons[i].style.display = 'none';
    }

    // 입력 필드
    const inputField = document.createElement('input');
    inputField.type = 'text';
    inputField.value = currentText;
    inputField.id = `update-${tagId}`;
    if (inputField.id.startsWith('update-main-category')) {
        inputField.style.width = "90%";
        inputField.style.fontSize = "1.4em";
        inputField.style.borderBottom = "2px solid #000";
    }
    else {
        inputField.style.width = "90%";
        inputField.style.height = "35px";
        inputField.style.fontSize = "1.1em";
    }

    // 에러 메세지
    const errorContainer = document.createElement('div');
    errorContainer.classList.add('invalid-feedback');

    // 저장 버튼
    const saveButton = document.createElement('button');
    saveButton.textContent = '저장';
    saveButton.type = 'submit'
    saveButton.classList.add('edit-btn', 'btn', 'btn-bd-primary');
    saveButton.onclick = function () {
        saveCategory(tagId, categoryId);
    };

    // 취소 버튼
    const cancelButton = document.createElement('button');
    cancelButton.textContent = '취소';
    cancelButton.classList.add('edit-btn', 'btn', 'cancel-btn');
    cancelButton.onclick = function () {
        cancelEditCategory(tagId, categoryId);
    };

    // 입력 필드, 에러메세지 컨테이너, 저장 삭제 버튼 추가
    parentElement.appendChild(inputField);
    parentElement.appendChild(errorContainer);
    btnContainer.appendChild(cancelButton);
    btnContainer.appendChild(saveButton);
}

// 저장 버튼 클릭 시
function saveCategory(tagId, categoryId) {
    // 수정된 카테고리 이름
    const inputField = document.getElementById(`update-${tagId}`);
    const newCategoryName = inputField.value.trim();

    // 유효성 검사
    if (!validateCategoryName(inputField)) {
        return;
    }

    console.log("#2 categoryId: " + categoryId);

    // 서버로 데이터 전송
    fetch('/api/v1/categories/update', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            categoryId: categoryId,
            categoryName: newCategoryName,
        }),
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 400) {  // BadRequest
                cancelEditCategory(tagId, categoryId);
                throw new Error('유효하지 않은 카테고리입니다(15자 이내 영대소문자, 한글, /만 가능).');
            } else if (response.status === 409) {  // Conflict
                cancelEditCategory(tagId, categoryId);
                return response.json().then(errorMessage => {throw errorMessage});
            } else { // 그 외 에러 처리
                cancelEditCategory(tagId, categoryId);
                throw new Error('카테고리 업데이트 실패에 실패했습니다.');
            }
        })
        .then(data  => {
            alert('카테고리가 업데이트되었습니다');
            // 수정된 화면 업데이트
            const categoryElement = document.getElementById(tagId);
            categoryElement.textContent = data.categoryName; // 변경된 값
            categoryElement.style.display = '';

            // 입력 필드, 저장 취소 버튼 제거
            inputField.remove(); // 입력 필드 제거
            // inputField.style.display = 'none'; // 입력 필드 숨김
            const parentElement = categoryElement.parentElement;
            const btnContainer = document.getElementById('btn-container-' + data.categoryId);
            const editButtons = btnContainer.querySelectorAll('.edit-btn');
            editButtons.forEach((button) => {
                button.remove();
            });

            // 수정, 삭제, 추가 버튼 복원
            const buttons = btnContainer.querySelectorAll('.update-btn, .delete-btn, .add-sub-category-btn');
            buttons.forEach(button => {
                button.style.display = '';
            });

        })
        .catch(error => {
            alert('에러 발생: ' + error.message);
        });
}

// 취소 버튼 클릭 시
function cancelEditCategory(tagId, categoryId) {
    // 기존 요소 복원
    const categoryElement = document.getElementById(tagId);
    // categoryElement.textContent = originalText;
    categoryElement.style.display = '';

    // 입력 필드, 에러 컨테이너 제거
    const inputField = document.getElementById(`update-${tagId}`);
    const errorContainer = inputField.nextElementSibling; // 에러 메시지 요소
    if (errorContainer) {
        errorContainer.remove();
    }
    inputField.remove();

    // 저장, 취소 버튼 제거
    const btnContainer = document.getElementById('btn-container-' + categoryId);
    const editButtons = btnContainer.querySelectorAll('.edit-btn');
    editButtons.forEach((button) => {
        button.remove();
    });

    // 수정, 삭제, 추가 버튼 복원
    const buttons = btnContainer.querySelectorAll('.update-btn, .delete-btn, .add-sub-category-btn');
    buttons.forEach(button => {
        button.style.display = '';
    });
}

// 카테고리 이름 유효성 검사 함수
function validateCategoryName(inputElement) {
    const errorContainer = inputElement.nextElementSibling; // 에러 메시지 요소
    const categoryName = inputElement.value.trim();
    const isValidCategoryName = /^[가-힣a-zA-Z/]+$/.test(categoryName); // 유효성 검사
    const isWithinLength = categoryName.length <= 15; // 15자 제한

    // 빈 값 or 유효성 검사 통과 X or 15자 초과
    if (!categoryName || !isValidCategoryName || !isWithinLength) {
        inputElement.classList.add('is-invalid');
        if (errorContainer) {
            if (!categoryName) {
                errorContainer.textContent = '카테고리 이름은 공백일 수 없습니다.';
            } else if (!isValidCategoryName) {
                errorContainer.textContent = '카테고리 이름은 한글, 영어 대소문자, /만 가능합니다.';
            } else if (!isWithinLength) {
                errorContainer.textContent = '카테고리 이름은 최대 15자까지 입력 가능합니다.';
            }
            errorContainer.style.display = 'block'; // 에러 메시지 표시
        }
        return false; // 유효성 검사 실패
    }

    // 유효성 검사 성공 시
    if (errorContainer) {
        errorContainer.remove();
    }
    return true; // 유효성 검사 성공
}