document.getElementById('productForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 제출 동작 방지

    let isValid = true;

    // 제품 이름 검증
    const productName = document.getElementById('productName');
    const productNameError = document.getElementById('productNameError');
    if (!productName.value) {
        productNameError.textContent = "제품 이름은 필수 입력 항목입니다.";
        isValid = false;
    } else if (productName.value.length > 20) {
        productNameError.textContent = "제품 이름은 최대 20자까지 가능합니다.";
        isValid = false;
    } else {
        productNameError.textContent = ""; // 에러 메시지 초기화
    }

    // 카테고리 검증
    const categoryName = document.getElementById('categoryName');
    const categoryNameError = document.getElementById('categoryNameError');
    if (!categoryName.value) {
        categoryNameError.textContent = "카테고리는 필수 입력 항목입니다.";
        isValid = false;
    } else {
        categoryNameError.textContent = "";
    }

    // 상세 설명 검증
    const description = document.getElementById('description');
    const descriptionError = document.getElementById('descriptionError');
    if (!description.value) {
        descriptionError.textContent = "상세 설명은 필수 입력 항목입니다.";
        isValid = false;
    } else if (description.value.length > 100) {
        descriptionError.textContent = "상세 설명은 최대 100자까지 가능합니다.";
        isValid = false;
    } else {
        descriptionError.textContent = "";
    }

    // 폼이 유효하면 서버로 제출
    if (isValid) {
        alert("폼이 유효합니다! 서버로 전송합니다.");
        // 실제 폼 제출
        this.submit();
    }
});
