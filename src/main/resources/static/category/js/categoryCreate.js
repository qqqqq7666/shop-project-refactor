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
                alert('에러 발생: ' + error.message);
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

    formContainer.reset(); // 폼 초기화
}