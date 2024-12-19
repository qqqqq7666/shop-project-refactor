let cart = [];

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

    cart.forEach((item, index) => {
        const li = document.createElement("li");
        li.innerHTML = `
            <span>${item.name}</span>
            <span>${item.price}원</span>
            <div class="quantity-controls">
                <button class="btn btn-primary" onclick="updateQuantity(${index}, -1)">-</button>
                <input type="text" value="${item.quantity}" readonly />
                <button class="btn btn-primary" onclick="updateQuantity(${index}, 1)">+</button>
            </div>
            <span>${item.price * item.quantity}원</span>
            <button class="btn btn-primary" onclick="removeItem(${index})">삭제</button>
        `;
        cartItems.appendChild(li);
        total += item.price * item.quantity;
        count += item.quantity;
    });

    itemCount.textContent = count;
    totalPrice.textContent = total.toLocaleString();
    finalPrice.textContent = (total + 3000).toLocaleString(); // 배송비 포함
}

// 장바구니에 상품 추가
function addItem(productId, name, price) {
    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({productId, name, price, quantity: 1 });
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
function updateQuantity(index, change) {
    const item = cart[index];
    if (item) {
        item.quantity = Math.max(1, item.quantity + change); // 최소 수량은 1
        saveCart();
        renderCart();
    }
}

// 장바구니 비우기
function clearCart() {
    cart = [];
    saveCart();
    renderCart();
}

// 더미 상품 데이터
const mockProducts = [
    { productId: 1, name: "케이블 라운드넥 니트", price: 29000 },
    { productId: 2, name: "씨빅 오리진 숏 푸퍼", price: 89000 },
    { productId: 3, name: "워싱 와이드 데님팬츠", price: 39000 }
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

// DOM이 준비되면 초기화
document.addEventListener("DOMContentLoaded", initializeCart);
document.getElementById("checkout").addEventListener("click", () => {
    if(localStorage.getItem("cart") === "[]")
        alert("장바구니가 비어있습니다.");
    else
        window.location.href = "/order/checkout";
})
