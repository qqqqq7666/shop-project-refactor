// import * as optionModule from "/static/js/productDetail"

let cart = [];

function loadCart() {
    const savedCart = localStorage.getItem("cart");
    if (savedCart) {
        cart = JSON.parse(savedCart);
    }
}

function addItem(productId, name, price) {
    const existingItem = cart.find(item => item.productId === productId);
    const optionText = document.querySelectorAll(".option-info span");
    let option = [];
    if(existingItem)
        option = existingItem.option;

    for (i = 0; i < optionText.length; i += 3){
        const optionArray = optionText[i].textContent.split(" ");
        console.log(optionArray);
        const size = optionArray[optionArray.length - 3].slice(1);
        const color = optionArray[optionArray.length - 1].slice(0, -1)
        const quantity = parseInt(document.querySelector(".option-info input").value);
        console.log(optionText[i + 1].textContent);

        const existOption = option.find(o => o.size === size && o.color === color)
        console.log(existOption);
        // 옵션 존재하면 수량만 변경
        if(existOption) {
            existOption.quantity += quantity;
        }
        else
            option.push({size, color, quantity});

    }

    // console.log(option);
    if (existingItem) {
        existingItem.option = option;
    } else
        cart.push({productId, name, price, option});

    saveCart();
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