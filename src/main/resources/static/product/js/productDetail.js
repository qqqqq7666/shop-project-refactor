document.addEventListener("DOMContentLoaded", () => {
    setTimeout(() => {
        fetch(`/products/detail/${productId}/confirm-view`, {
            method: "POST"
        })
            .then(response => {
                if (response.ok) {
                    console.log("View count incremented");
                } else {
                    console.log("View count not incremented");
                }
            })
            .catch(error => console.error("Error:", error));
    }, 5000); // 5초 대기
});

// 선택된 상품 옵션 목록
const selectedOptions = [];
const colorSelect = document.getElementById("colorSelect");
const sizeSelect = document.getElementById("sizeSelect");

colorSelect.addEventListener("change", function () {
    const productData = document.getElementById('productData');
    const color = colorSelect.value;
    const productId = productData.dataset.productId; // data-product-id에서 제품 ID 가져오기

    console.log("Selected color:", color);
    console.log("Product ID:", productId);

    if (!color) {
        return; // 색상이 선택되지 않은 경우 아무 작업도 하지 않음
    }

    fetch(`/api/products/available-sizes?productId=${productId}&color=${color}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("사이즈 데이터를 가져오지 못했습니다.");
            }
            return response.json();
        })
        .then((data) => {
            console.log("받은 데이터:", data); // API 데이터 확인용
            updateSizeOptions(data); // 사이즈 옵션 업데이트
        })
        .catch((error) => {
            console.error("에러 발생:", error);
            alert("사이즈 데이터를 가져오는 중 문제가 발생했습니다.");
        });
});


// 기존에 사이즈와 색상이 중복되는 경우 모두 불러와지는 경우 발생, 수정
document.addEventListener("DOMContentLoaded", () => {
    if (typeof productOptions === 'undefined') {
        console.error("productOptions가 정의되지 않았습니다.");
        return;
    }
    console.log("초기 productOptions:", productOptions);

    // 초기화
    sizeSelect.innerHTML = '<option value="">사이즈를 선택하세요</option>';
    sizeSelect.disabled = true;

    // 중복 제거 함수
    function getUniqueColors(options) {
        const seenColors = new Set();
        return options.filter(option => {
            if (seenColors.has(option.color)) {
                return false; // 이미 본 색상은 제거
            }
            seenColors.add(option.color);
            return true; // 새로운 색상만 유지
        });
    }

    // 색상 옵션 렌더링
    function renderColorOptions() {
        colorSelect.innerHTML = '<option value="">색상을 선택하세요</option>';
        const uniqueColors = getUniqueColors(productOptions);

        uniqueColors.forEach(option => {
            const colorOption = document.createElement("option");
            colorOption.value = option.color;
            colorOption.textContent = option.color;
            colorSelect.appendChild(colorOption);
        });
    }

    // 초기 색상 옵션 렌더링
    renderColorOptions();

    // 색상이 선택되었을 때 동작
    colorSelect.addEventListener("change", () => {
        const selectedColor = colorSelect.value;

        // 사이즈 선택 초기화
        sizeSelect.innerHTML = '<option value="">사이즈를 선택하세요</option>';

        if (selectedColor) {
            // 해당 색상에 맞는 사이즈 필터링
            const availableSizes = productOptions
                .filter(option => option.color === selectedColor)
                .map(option => option.size);

            // 중복 제거 후 사이즈 옵션 추가
            [...new Set(availableSizes)].forEach(size => {
                const optionElement = document.createElement("option");
                optionElement.value = size;
                optionElement.textContent = size;
                sizeSelect.appendChild(optionElement);
            });

            // 사이즈 선택 활성화
            sizeSelect.disabled = false;
        } else {
            // 색상이 선택되지 않은 경우 비활성화
            sizeSelect.disabled = true;
        }
    });
});


// 이벤트 리스너 등록
sizeSelect.addEventListener("change", handleOptionChange);
// colorSelect.addEventListener("change", handleOptionChange);

// 옵션 변경 시 처리 함수
function handleOptionChange() {
    const productName = document.querySelector("h1").innerText;
    const size = sizeSelect.value;
    const color = colorSelect.value;
    const price = parseInt(document.getElementById("productPrice").dataset.price);

    if (!size || !color) {
        return; // 둘 다 선택되지 않으면 아무 작업도 하지 않음
    }

    // 기존 선택된 옵션 확인
    // const existingSize = selectedOptions.find((option) => option.size === size);
    //
    // // 색상만 변경된 경우 처리
    // if (existingSize && existingSize.color !== color) {
    //     return; // 색상만 변경되었을 때는 추가하지 않음
    // }

    const optionId = `${productName}-${size}-${color}`;

    // 중복 방지
    const existingOption = selectedOptions.find((option) => option.id === optionId);
    if (existingOption) {
        alert("이미 추가된 항목입니다.");
        return; // 이미 선택된 옵션이면 추가하지 않음
    }

    // 새로운 옵션 추가
    const newOption = {
        id: optionId,
        name: productName,
        size,
        color,
        price,
        quantity: 1, // 기본 수량 1
    };

    selectedOptions.push(newOption);
    renderSelectedOptions();
}


// 선택된 옵션 목록 렌더링
function renderSelectedOptions() {
    const selectedOptionsContainer = document.getElementById("selectedOptions");
    selectedOptionsContainer.innerHTML = ""; // 기존 내용을 초기화

    let totalQuantity = 0;
    let totalPrice = 0;

    selectedOptions.forEach((option, index) => {
        totalQuantity += option.quantity;
        totalPrice += option.quantity * option.price;

        const optionElement = document.createElement("div");
        optionElement.classList.add("selected-option");
        optionElement.innerHTML = `
            <div class="option-info">
                <span>${option.name}<br> (${option.size} - ${option.color})</span>
                <div class="d-flex"> <br>
                    <button class="round-button" onclick="changeQuantity(${index}, -1)">-</button>
                    &nbsp&nbsp
                    <input type="text" value="${option.quantity}" readonly />
                    &nbsp&nbsp
                    <button class="round-button" onclick="changeQuantity(${index}, 1)">+</button>
                </div>
                <span class="option-price">₩${(option.quantity * option.price).toLocaleString()}</span>
                <button class="delete-button" onclick="removeOption(${index})">x</button>
            </div>
        `;

        selectedOptionsContainer.appendChild(optionElement);
    });

    const totalContainer = document.getElementById("totalContainer");
    totalContainer.innerHTML = `TOTAL: ₩${totalPrice.toLocaleString()} (${totalQuantity}개)`;
}

// 수량 변경 함수
function changeQuantity(index, amount) {
    const option = selectedOptions[index];
    option.quantity = Math.max(1, option.quantity + amount); // 수량은 최소 1
    renderSelectedOptions();
}

// 선택된 옵션 삭제
function removeOption(index) {
    selectedOptions.splice(index, 1); // 선택 목록에서 제거
    renderSelectedOptions(); // 다시 렌더링
}

// 품절 처리 함수
function updateSizeOptions(sizeData) {
    // 기존 사이즈 옵션 초기화
    sizeSelect.innerHTML = '<option value="">사이즈를 선택하세요</option>';

    sizeData.forEach((sizeInfo) => {
        const option = document.createElement("option");
        option.value = sizeInfo.size;
        option.textContent = sizeInfo.size;

        // 재고가 없는 경우 비활성화
        if (sizeInfo.stockQuantity === 0) {
            option.disabled = true;
            option.textContent += " (품절)";
        }

        sizeSelect.appendChild(option);
        console.log("업데이트된 사이즈 옵션:", sizeSelect.innerHTML); // 디버깅용
    });
}

