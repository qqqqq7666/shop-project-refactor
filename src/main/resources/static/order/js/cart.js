let cart = [];
let currentProductOption;
// 로컬스토리지에서 데이터 가져오기
function loadCart() {
    const savedCart = localStorage.getItem("cart");
    if (savedCart) {
        cart = JSON.parse(savedCart);
    }
    renderCart();
}

// 로컬스토리지에 데이터 저장하기
function saveCart() {
    localStorage.setItem("cart", JSON.stringify(cart));
}

// 장바구니 UI 렌더링
function renderCart() {
    const cartItems = document.getElementById("cart-items");
    const itemCount = document.getElementById("item-count");
    const totalPrice = document.getElementById("total-price");
    const finalPrice = document.getElementById("final-price");

    cartItems.innerHTML = "";
    let total = 0;
    let count = 0;

    cart.forEach(async (item, index) => {
        item.option.forEach((option, optionIndex) => {
            const li = document.createElement("li");
            li.innerHTML = `
            <div>
            <img src="/images/imageInfo.png" width="100px">
            </div>
            <div align="center">
            <span>${item.name}<br><br></span>
            <span>${item.price}원</span>
            </div>
            <h6>X</h6>
            <div class="quantity-controls" align="center">
            <span>(${option.size} / ${option.color})<br>
                <button class="btn btn-primary" onclick="updateQuantity(${index}, ${optionIndex}, -1)">-</button>
                <input type="text" value="${option.quantity}" readonly />
                <button class="btn btn-primary" onclick="updateQuantity(${index}, ${optionIndex}, 1)">+</button>
                <br>
                <button type="button" id="${index}-${optionIndex}" class="btn btn-primary option-change-button option${index}" data-bs-toggle="modal" data-bs-target="#exampleModal">
                    옵션 변경
                </button>
            </span>
            </div>
            <h3>=</h3>
            <span>${item.price * option.quantity}원</span>
            <div>
            <button class="btn btn-primary" onclick="removeOption(${index}, ${optionIndex})">삭제</button>
            </div>
        `;
            cartItems.appendChild(li);
            total += option.quantity * item.price;

        });

        // 상품 옵션 변경 모달 render
        const response = await fetch(`/api/v1/orders/product-option/${cart[index].productId}`);
        const productOption = await response.json();
        document.body.addEventListener("click", event => {
            if (event.target.classList.contains(`option${index}`)) {
                document.getElementById("option-size").innerHTML = ""
                document.getElementById("option-color").innerHTML = ""
                console.log(event.target.id);
                productOption.options.forEach(option => {
                    const optionSize = document.createElement("option");
                    const optionColor = document.createElement("option");

                    optionSize.value = option.size;
                    optionColor.value = option.color;
                    optionSize.textContent = option.size;
                    optionColor.textContent = option.color;

                    document.getElementById("option-size").appendChild(optionSize);
                    document.getElementById("option-color").appendChild(optionColor);

                    currentProductOption = event.target.id;
                });
            }
        });

        cartItems.getEvent
    });


    itemCount.textContent = cart.length.toLocaleString();
    totalPrice.textContent = total.toLocaleString();
    finalPrice.textContent = (total + 3000).toLocaleString(); // 배송비 포함
}

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
    const testButton = document.getElementById("test-add-products");
    if (testButton) {
        testButton.addEventListener("click", mockAddProductsToCart);
    }
    loadCart();
}

// 장바구니 상품 옵션 변경
function changeProductOption(cartIndex, optionIndex) {
    const toChangeSize = document.getElementById("option-size").value;
    const toChangeColor = document.getElementById("option-color").value;
    if (cart[cartIndex].option.find(option => option.size === toChangeSize && option.color === toChangeColor)){
        alert("동일한 옵션의 상품이 존재합니다.");
    } else {
        cart[cartIndex].option[optionIndex].size = toChangeSize;
        cart[cartIndex].option[optionIndex].color = toChangeColor;
        saveCart();
        alert("상품 옵션이 변경되었습니다.");
        document.getElementById("cancel-modal").click();
        renderCart();
    }
}

// DOM이 준비되면 초기화
document.addEventListener("DOMContentLoaded", initializeCart);
document.getElementById("checkout").addEventListener("click", () => {
    if (localStorage.getItem("cart") === "[]" || localStorage.getItem("cart") === null)
        alert("장바구니가 비어있습니다.");
    else
        window.location.href = "/order/checkout";
})
document.getElementById("option-save-button").addEventListener("click", () => {
    const indexSet = currentProductOption.split("-");
    const cartIndex = parseInt(indexSet[0]);
    const optionIndex = parseInt(indexSet[1]);
    changeProductOption(cartIndex, optionIndex);
});