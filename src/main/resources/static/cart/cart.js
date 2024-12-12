// 전역 변수
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

    cart.forEach((item, index) => {
        const li = document.createElement("li");
        li.innerHTML = `
            <span>${item.name}</span>
            <span>${item.price}원 x ${item.quantity}</span>
            <button onclick="removeItem(${index})">삭제</button>
        `;
        cartItems.appendChild(li);
        total += item.price * item.quantity;
    });

    itemCount.textContent = cart.length;
    totalPrice.textContent = total;
    finalPrice.textContent = total + 3000; // 배송비 포함
}

// 장바구니에 상품 추가
// id로 상품 식별 및 id도 로컬스토리지에 추가
function addItem(id, name, price) {
    const existingItem = cart.find(item => item.id === id);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({id, name, price, quantity: 1 });
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

// 장바구니 비우기
function clearCart() {
    cart = [];
    saveCart();
    renderCart();
}

// 더미 상품 데이터
const mockProducts = [
    { id: 1, name: "케이블 라운드넥 니트", price: 29000 },
    { id: 2, name: "씨빅 오리진 숏 푸퍼", price: 89000 },
    { id: 3, name: "워싱 와이드 데님팬츠", price: 39000 }
];

// 더미 상품 추가 함수
function mockAddProductsToCart() {
    mockProducts.forEach(product => addItem(product.id, product.name, product.price));
}

// 초기화
function initializeCart() {
    // 장바구니 비우기 버튼 연결
    document.getElementById("clear-cart").addEventListener("click", clearCart);

    // 더미 상품 추가 버튼 연결
    const testButton = document.getElementById("test-add-products");
    if (testButton) {
        testButton.addEventListener("click", mockAddProductsToCart);
    }

    loadCart();
}

// DOM이 준비되면 초기화
document.addEventListener("DOMContentLoaded", initializeCart);