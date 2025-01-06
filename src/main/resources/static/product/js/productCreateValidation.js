document.getElementById('productForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 제출 동작 방지

    let isValid = true;

    // 제품 이름 검증
    const productName = document.getElementById('productName');
    const productNameError = document.getElementById('productNameError');
    if (!productName.value) {
        productNameError.textContent = "제품 이름은 필수 입력 항목입니다.";
        isValid = false;
    } else if (productName.value.length > 50) {
        productNameError.textContent = "제품 이름은 최대 50자까지 가능합니다.";
        isValid = false;
    } else {
        productNameError.textContent = ""; // 에러 메시지 초기화
    }

    // 카테고리 검증
    /* const categoryName = document.getElementById('categoryName');
    const categoryNameError = document.getElementById('categoryNameError');
    if (!categoryName.value) {
        categoryNameError.textContent = "카테고리는 필수 입력 항목입니다.";
        isValid = false;
    } else {
        categoryNameError.textContent = "";
    } */

    // 상세 설명 검증
    const description = document.getElementById('description');
    const descriptionError = document.getElementById('descriptionError');
    if (!description.value) {
        descriptionError.textContent = "상세 설명은 필수 입력 항목입니다.";
        isValid = false;
    } else if (description.value.length > 500) {
        descriptionError.textContent = "상세 설명은 최대 500자까지 가능합니다.";
        isValid = false;
    } else {
        descriptionError.textContent = "";
    }

    // 가격 검증
    const price = document.getElementById('price');
    const priceError = document.getElementById('priceError');
    if (!price.value) {
        priceError.textContent = "가격은 필수 입력 항목입니다.";
        isValid = false;
    } else if (price.value.length < 0) {
        priceError.textContent = "가격은 0원 이상이어야 합니다.";
        isValid = false;
    } else {
        priceError.textContent = "";
    }

    // 색상 및 재고 검증
    const colorInputs = document.querySelectorAll('input[name="colors[]"]');
    const stockInputs = document.querySelectorAll('input[name="stocks[]"]');
    let hasColorError = false;
    let hasStockError = false;

    colorInputs.forEach((input, index) => {
        const errorElement = input.parentElement.querySelector('.error-message'); // 색상 필드 아래의 에러 메시지
        if (!input.value.trim() || input.value==="") {
            console.log("input.value : ", input.value);
            errorElement.textContent = "색상은 필수 입력 항목입니다.";
            isValid = false;
            hasColorError = true;
        } else {
            errorElement.textContent = "";
        }
    });

    stockInputs.forEach((input, index) => {
        const errorElement = input.parentElement.querySelector('.error-message'); // 재고 필드 아래의 에러 메시지
        if (!input.value || input.value <= 0) {
            errorElement.textContent = "재고는 1 이상이어야 합니다.";
            isValid = false;
            hasStockError = true;
        } else {
            errorElement.textContent = "";
        }
    });

});
