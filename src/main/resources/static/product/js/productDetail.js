document.addEventListener("DOMContentLoaded", () => {
    setTimeout(() => {
        fetch('/products/detail/${productId}/confirm-view', {
            method: "POST"
        })
            .then(response => {
                if (response.ok) {
                    console.log("View count incremented");
                } else {
                    console.log("View count not incremented");
                }
            });
    }, 5000); // 5초 대기
});

// 선택된 상품 옵션 목록
const selectedOptions = [];

// 기존에 사이즈와 색상이 중복되는 경우 모두 불러와지는 경우 발생, 수정
window.onload = function() {
    // 사이즈와 색상 select 요소 가져오기
    const sizeSelect = document.getElementById("sizeSelect");
    const colorSelect = document.getElementById("colorSelect");

    // 중복 제거 함수
    function removeDuplicateOptions(selectElement) {
        const seen = new Set();
        [...selectElement.options].forEach(option => {
            if (seen.has(option.value) && option.value !== "") {
                option.remove(); // 중복된 옵션 제거
            } else {
                seen.add(option.value);
            }
        });
    }

    // 중복 제거 실행
    removeDuplicateOptions(sizeSelect);
    removeDuplicateOptions(colorSelect);
};

// 이벤트 리스너 등록
sizeSelect.addEventListener("change", handleOptionChange);
colorSelect.addEventListener("change", handleOptionChange);

// 옵션 변경 시 처리 함수
function handleOptionChange() {
    const productName = document.querySelector("h1").innerText;
    const size = sizeSelect.value;
    const color = colorSelect.value;
    const price = parseInt(document.getElementById("productPrice").dataset.price);

    if (!size || !color) {
        return; // 둘 다 선택되지 않으면 아무 작업도 하지 않음
    }

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
                <span>${option.name} (${option.size} - ${option.color})</span>
                <div class="quantity-controls">
                    <button onclick="changeQuantity(${index}, -1)">-</button>
                    <span>${option.quantity}</span>
                    <button onclick="changeQuantity(${index}, 1)">+</button>
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
