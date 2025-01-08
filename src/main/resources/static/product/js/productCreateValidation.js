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

    // 사이즈, 색상, 재고 유효성 검사
    const sizeColorStockGroups = document.querySelectorAll('.size-color-stock-group');
    sizeColorStockGroups.forEach((group) => {
        const sizeSelect = group.querySelector('select[name="sizes[]"]');
        const colorInput = group.querySelector('input[name="colors[]"]');
        const stockInput = group.querySelector('input[name="stocks[]"]');
        const errorMessage = group.querySelector('.error-message');

        // 초기화
        errorMessage.textContent = '';

        // 사이즈 선택 확인
        if (!sizeSelect.value) {
            errorMessage.textContent = '사이즈를 선택하세요.';
            isValid = false;
        }
        // 색상 입력 확인
        else if (!colorInput.value.trim()) {
            errorMessage.textContent = '색상을 입력하세요.';
            isValid = false;
        }
        // 재고 입력 확인
        else if (!stockInput.value || stockInput.value < 0) {
            errorMessage.textContent = '유효한 재고를 입력하세요.';
            isValid = false;
        }
    });

});
