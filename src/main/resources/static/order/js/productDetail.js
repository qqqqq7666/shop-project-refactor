let cart = [];

function loadCart() {
    const savedCart = localStorage.getItem("cart");
    if (savedCart) {
        cart = JSON.parse(savedCart);
    }
}

function addItem(productId, name, price) {
    const existingItem = cart.find(item => item.productId === productId);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        console.log(productId);
        cart.push({productId, name, price, quantity: 1 });
    }
    saveCart();
    // renderCart();
}

function saveCart() {
    localStorage.setItem("cart", JSON.stringify(cart));
}

function initializeCart() {
    const testButton = document.getElementById("test-add-products");
    if (testButton) {
        testButton.addEventListener("click", mockAddProductsToCart);
    }
    loadCart();
}

// DOM이 준비되면 초기화
document.addEventListener("DOMContentLoaded", initializeCart);