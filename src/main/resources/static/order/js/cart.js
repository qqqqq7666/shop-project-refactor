let cart = [];
let currentProductOption;
const optionEventFlag = true;

// 로컬스토리지에서 데이터 가져오기
function loadCart() {
    const savedCart = localStorage.getItem("cart");
    cart = savedCart ? JSON.parse(savedCart).filter(item => item.option.length !== 0) : [];
    renderCart();
}


// 로컬스토리지에 데이터 저장하기
function saveCart() {
    localStorage.setItem("cart", JSON.stringify(cart));
}

// 장바구니 UI 렌더링
async function renderCart() {
    const cartItems = document.getElementById("cart-items");
    const itemCount = document.getElementById("item-count");
    const totalPrice = document.getElementById("total-price");
    const finalPrice = document.getElementById("final-price");

    cartItems.innerHTML = "";
    let total = 0;
    let count = 0;
    for (const [index, item] of cart.entries()) {
        const productOption = await (await fetch(`/api/v1/orders/product-option/${item.productId}`)).json();
        for (const [optionIndex, option] of item.option.entries()) {
            const cartItemHTML = await createCartItemTemplate(item, option, index, optionIndex, productOption);
            cartItems.insertAdjacentHTML("beforeend", cartItemHTML);
            total += option.quantity * item.price;
            count += option.quantity;
        }

        await productOptionChangeEvent(item, index, productOption);
    }

    // 최종 출력
    itemCount.textContent = count.toLocaleString();
    totalPrice.textContent = total.toLocaleString();
    if (total < 50000)
        total += 3000;

    finalPrice.textContent = (total).toLocaleString(); // 배송비 포함
}

async function productOptionChangeEvent(item, index, productOption){
    // 상품 옵션 변경 모달 render
    const selectSize = document.getElementById("option-size");
    const selectColor = document.getElementById("option-color");

    // 상품 옵션 변경 처리
    document.body.addEventListener("click", event => {
        if (event.target.classList.contains(`option${index}`)) {
            selectColor.innerText = "";
            selectSize.innerText = "";
            // 옵션 선택 초기화
            // console.log(event.target.id);
            const colorSet = new Set();
            productOption.options.forEach(option => {
                if(!colorSet.has(option.color)) {
                    const optionColor = document.createElement("option");

                    optionColor.value = option.color;
                    optionColor.textContent = option.color;

                    selectColor.appendChild(optionColor);
                    colorSet.add(option.color);
                }

                currentProductOption = event.target.id;
            });
            sizeFilterByColor(productOption, selectColor, selectSize);

            // TODO 이벤트가 계속 추가되는거 고쳐야됨
            selectColor.addEventListener("change", () => {
                sizeFilterByColor(productOption, selectColor, selectSize);
            });
        }
    });
}

function sizeFilterByColor(productOption, selectColor, selectSize) {
    selectSize.innerText = "";
    console.log(`필터된 옵션 ${productOption.options.filter(option => option.color === selectColor.value).toString()}`)
    productOption.options.filter(option => option.color === selectColor.value).forEach(option => {
        console.log(`사이즈 출력하기 ${option.size}`);
        const optionSize = document.createElement("option");

        optionSize.value = option.size;
        optionSize.textContent = option.size;

        selectSize.appendChild(optionSize);
    });
}

async function createCartItemTemplate(item, option, index, optionIndex, productOption) {
    const li = document.createElement("li");
    const productImage = productOption.imageUrls[0]
    return li.innerHTML = `
<li class="cart-item">
    <img src="${productImage}" class="img-thumbnail" width="100px" height="100px">
    <div align="center" class="d-flex flex-column justify-content-center align-items-center">
        <span style="font-size: 20px">${item.name}</span> 
        <span style="color: gray"> (${option.size} / ${option.color})</span>
        <span>${item.price.toLocaleString()}원</span>
        <button type="button" id="${index}-${optionIndex}" class="btn option-change-button option${index}" data-bs-toggle="modal" data-bs-target="#exampleModal"
            style="width: 100px; height: 30px; line-height: 10px">
            옵션 변경
        </button>
    </div>
    <h6>X</h6>
    <div class="quantity-controls d-flex flex-column">
        <div class="d-flex"><br>
            <button class="round-button" onclick="updateQuantity(${index}, ${optionIndex}, -1)">-</button>
            &nbsp&nbsp
            <input type="text" value="${option.quantity}" readonly />
            &nbsp&nbsp
            <button class="round-button" onclick="updateQuantity(${index}, ${optionIndex}, 1)">+</button>
        </div>
    </div>
    <h3>=</h3>
    <span class="price">${(item.price * option.quantity).toLocaleString()}원</span>
    <div class="d-flex align-items-start justify-content-center flex-column">
        <button class="btn btn-secondary" onclick="removeOption(${index}, ${optionIndex})">삭제</button>
    </div>
</li>
            `;
}

// 색상 선택 이후 선택한 색상을 기준으로 option.color를 찾아서

// 장바구니에 상품 추가
function addItem(productId, name, price) {
    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({productId, name, price, quantity: 1});
    }
    saveCart();
    renderCart();
}

// 장바구니에서 상품 삭제
function removeItem(index) {
    cart.splice(index, 1);
    saveCart();
    renderCart();
}

// 수량 업데이트
function updateQuantity(index, optionIndex, change) {
    const item = cart[index].option[optionIndex];
    if (item) {
        item.quantity = Math.max(1, item.quantity + change); // 최소 수량은 1
        saveCart();
        renderCart();
    }
}

function removeOption(index, optionIndex) {
    cart[index].option.splice(optionIndex, 1);
    saveCart();
    countInit();
    renderCart();
}

// 장바구니 비우기
function clearCart() {
    cart = [];
    saveCart();
    renderCart();
}

// 더미 상품 데이터
const mockProducts = [
    {productId: 1, name: "케이블 라운드넥 니트", price: 29000},
    {productId: 2, name: "씨빅 오리진 숏 푸퍼", price: 89000},
    {productId: 3, name: "워싱 와이드 데님팬츠", price: 39000}
];

// 더미 상품 추가 함수
function mockAddProductsToCart() {
    mockProducts.forEach(product => addItem(product.productId, product.name, product.price));
}

// 초기화
function initializeCart() {
    document.getElementById("clear-cart").addEventListener("click", clearCart);
    loadCart();

    changeProgressbar();
    deliveryFree();
}

// 장바구니 상품 옵션 변경
function changeProductOption(cartIndex, optionIndex) {
    const newSize = document.getElementById("option-size").value;
    const newColor = document.getElementById("option-color").value;
    if (cart[cartIndex].option.some(option => option.size === newSize && option.color === newColor)) {
        alert("동일한 옵션의 상품이 존재합니다.");
    } else {
        cart[cartIndex].option[optionIndex].size = newSize;
        cart[cartIndex].option[optionIndex].color = newColor;
        saveCart();
        alert("상품 옵션이 변경되었습니다.");
        document.getElementById("cancel-modal").click();
        renderCart();
    }
}

function changeProgressbar() {
    document.getElementById("progress-bar").style.width = `${parseInt(document.getElementById("total-price").textContent.replace(/,/g, '')) / 500}%`
}

function deliveryFree() {
    if (parseInt(document.getElementById("total-price").textContent.replace(/,/g, '')) >= 50000) {
        document.getElementById("delivery-fee-div").classList.add("hidden");
        document.getElementById("delivery-free").textContent = "배송비 무료";
    } else {
        document.getElementById("delivery-fee-div").classList.remove("hidden");
        document.getElementById("delivery-free").textContent = "";
    }
}

// DOM이 준비되면 초기화
document.addEventListener("DOMContentLoaded", initializeCart);
document.getElementById("checkout").addEventListener("click", () => {
    if (cart.length === 0)
        alert("장바구니가 비어있습니다.");
    else
        window.location.href = "/order/checkout";
})
document.getElementById("option-save-button").addEventListener("click", () => {
    const [cartIndex, optionIndex] = currentProductOption.split("-").map(Number);
    changeProductOption(cartIndex, optionIndex);
});

const totalPriceText = document.getElementById("total-price");

// MutationObserver 생성
const observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
        if (mutation.type === "characterData" || mutation.type === "childList") {
            changeProgressbar(); // textContent가 변경되면 실행될 함수
            deliveryFree();
        }
    });
});

// MutationObserver 설정
observer.observe(totalPriceText, {
    childList: true, // 자식 노드 변경 감지
    characterData: true, // 텍스트 내용 변경 감지
    subtree: true // 하위 노드까지 감지
});

if(localStorage.getItem("cart") === null || localStorage.getItem("cart") === "[]")
    document.getElementById("product-info-container").innerText = "장바구니가 비어 있습니다."