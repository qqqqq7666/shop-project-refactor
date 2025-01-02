// 카테고리 폼 제출
document.body.addEventListener('click', (event) => {
    if (event.target.matches('button[type="submit"]')) {
        let form = event.target.closest('form');
        if (!form) return; // 폼이 없으면 종료
        console.log('Form ID:', form.id);

        const mainCategoryNameInput = form.querySelector('input[name="mainCategoryName"]');
        console.log(mainCategoryNameInput.value);
        const subCategoryNameInput = form.querySelector('input[name="subCategoryName"]');
        console.log(subCategoryNameInput.value);

        const isMainValid = validateCategoryName(mainCategoryNameInput);
        console.log(isMainValid);
        const isSubValid = validateCategoryName(subCategoryNameInput);
        console.log(isSubValid);

        if (!isMainValid || !isSubValid) {
            event.preventDefault(); // 기본 제출 동작 중단
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

        fetch('/api/v1/categories/create', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                const activeElement = document.querySelector('.active');
                const activeElementId = activeElement.id;

                if (response.ok) {
                    return response.json();
                } else if (response.status === 400) {  // BadRequest
                    toggleForm(activeElementId);
                    throw new Error('유효하지 않은 카테고리입니다(15자 이내 영대소문자, 한글, /만 가능).');
                } else if (response.status === 409) {  // Conflict
                    toggleForm(activeElementId);
                    return response.json().then(errorMessage => {throw errorMessage});
                } else { // 그 외 에러 처리
                    toggleForm(activeElementId);
                    throw new Error('카테고리 추가에 실패했습니다.');
                }
            })
            .then(data => {
                console.log('Success:', data);
                alert('카테고리가 추가되었습니다.');
                addCategoryToUI(data);
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message);
            });
    }
});

function addCategoryToUI(category) {
    const existingMainCategory = document.getElementById(`main-category-${category.categoryId}`);

    if (existingMainCategory) { // 서브 카테고리 추가
        addSubCategoryToUI(category.categoryId, category.subCategories[category.subCategories.length - 1]);
        toggleForm(`sub-category-form-${category.categoryId}`);
    } else { // 메인 카테고리 추가
        const mainCategoryDiv = document.createElement('div');
        mainCategoryDiv.classList.add('main-category');
        mainCategoryDiv.innerHTML = `
            <div>
                <p id="main-category-${category.categoryId}">${category.categoryName}</p>
                <button class="update-btn" onclick="editCategory('main-category-${category.categoryId}', '${category.categoryId}')">수정</button>
            </div>
            <ul id="sub-category-list-${category.categoryId}"></ul>
            <form id="sub-category-form-${category.categoryId}" class="form-container">
                <input type="hidden" name="mainCategoryName" value="${category.categoryName}">
                <label for="sub-form-category-name-${category.categoryId}">서브 카테고리 추가</label><br>
                <input type="text" id="sub-form-category-name-${category.categoryId}" name="subCategoryName" placeholder="서브 카테고리 이름을 입력하세요" required><br>
                <button type="submit">저장</button>
                <button type="button" onclick="toggleForm('sub-category-form-${category.categoryId}')">취소</button>
            </form>
            <button class="add-sub-category-btn" onclick="toggleForm('sub-category-form-${category.categoryId}')">+</button>
        `;
        document.getElementById('category-list').appendChild(mainCategoryDiv);

        // 서브 카테고리 추가
        if (category.subCategories && category.subCategories.length > 0) {
            addSubCategoryToUI(category.categoryId, category.subCategories[category.subCategories.length - 1]);
        }
        toggleForm('main-category-form');
    }
}

function addSubCategoryToUI(mainCategoryId, subCategory) {
    if (!subCategory) return; // 서브 카테고리가 없는 경우 바로 리턴

    const subCategoryList = document.getElementById(`sub-category-list-${mainCategoryId}`);

    // 중복된 서브 카테고리 확인
    if (!document.getElementById(`sub-category-${subCategory.categoryId}`)) {
        const subCategoryItem = document.createElement('li');
        subCategoryItem.classList.add('sub-category');
        subCategoryItem.innerHTML = `
            <span id="sub-category-${subCategory.categoryId}">${subCategory.categoryName}</span>
            <button class="update-btn" onclick="editCategory('sub-category-${subCategory.categoryId}', '${subCategory.categoryId}')">수정</button>
            <button class="delete-btn" onclick="deleteSubCategory('${subCategory.categoryId}')">삭제</button>
        `;
        subCategoryList.appendChild(subCategoryItem);
    }
}

function toggleForm(formId) {
    const formContainer = document.getElementById(formId);
    formContainer.classList.toggle('active'); // 폼 활성화/비활성화

    // 카테고리 추가 버튼 처리
    const parentElement = formContainer.parentElement;
    const addButton = parentElement.querySelector('.add-btn');
    if (addButton) {
        if (formContainer.classList.contains('active')) {
            addButton.style.display = 'none';
        } else {
            addButton.style.display = '';
        }
    }

    // 유효성 검사 클래스, 에러 메세지 초기화
    formContainer.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    formContainer.querySelectorAll('.invalid-feedback').forEach(el => el.style.display = 'none');

    formContainer.reset(); // 폼 초기화
}

// 카테고리 이름 유효성 검사 함수
function validateCategoryName(inputElement) {
    // 서브 카테고리 생성 시 메인 카텥고리 검사는 pass
    if (inputElement.type === 'hidden') {
        return true; // 유효성 검사 통과
    }
    const errorContainer = inputElement.nextElementSibling; // 에러 메시지 요소
    const categoryName = inputElement.value.trim();
    const isValidCategoryName = /^[가-힣a-zA-Z/]+$/.test(categoryName); // 유효성 검사

    // 빈 값 or 유효성 검사 통과 X
    if (!categoryName || !isValidCategoryName) {
        inputElement.classList.add('is-invalid');
        if (errorContainer) {
            errorContainer.textContent = '카테고리 이름은 한글, 영어 대소문자, /만 가능하며 공백일 수 없습니다.';
            errorContainer.style.display = 'block'; // 에러 메시지 표시
        }
        return false; // 유효성 검사 실패
    }

    // 유효성 검사 성공
    inputElement.classList.remove('is-invalid');
    if (errorContainer) {
        errorContainer.style.display = 'none'; // 에러 메시지 숨김
    }
    return true; // 유효성 검사 성공
}