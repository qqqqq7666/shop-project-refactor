// Example starter JavaScript for disabling form submissions if there are invalid fields
let formChecked = false;
let requirement;

// 유효성 검사
(() => {
    'use strict'

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll('.needs-validation')

    // Loop over them and prevent submission
    Array.from(forms).forEach(form => {
        form.addEventListener('change', event => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
                formChecked = false;
            } else {
                formChecked = true;
            }
            form.classList.add('was-validated')
        }, false)
    })

    if(localStorage.getItem("cart") === "[]" || localStorage.getItem("cart") === null){
        alert("잘못된 접근입니다.");
        window.location.href = "/";    // 홈으로 바꿀 예정
    }

})();

const cartList = document.getElementById("cart-list");
const productArr = JSON.parse(localStorage.getItem("cart"));
let total = 0;

// 결제 품목 표시
function renderProduct() {

    for (i = 0; i < productArr.length; i++) {
        const product = document.createElement("li");
        product.classList.add("list-group-item", "d-flex", "justify-content-between", "lh-sm");
        product.innerHTML = '<div>\n' +
            '              <h6 class="my-0 product-name">Product name</h6>\n' +
            '              <small class="text-body-secondary product-quantity">x2</small>\n' +
            '            </div>\n' +
            '            <span class="text-body-secondary product-price">30원</span>';

        const price = productArr[i].price;
        const quantity = productArr[i].quantity;

        product.getElementsByClassName("product-name")[0].innerText = productArr[i].name;
        product.getElementsByClassName("product-price")[0].innerText = `${(price * quantity).toLocaleString()}원`;
        product.getElementsByClassName("product-quantity")[0].innerText = `x${quantity}`;

        cartList.prepend(product);
        total += price * quantity;
    }
    if (total >= 50000) {
        const discount = document.getElementById("discount");
        discount.style.removeProperty("display");
    } else
        total += 3000;

    document.getElementById("total").innerText = total.toLocaleString();
}

function checkout() {
    if (formChecked && confirm("주문 하시겠습니까?")) {
        if (requirementSelect.value === "직접입력")
            requirement = requireTextarea.value;
        else
            requirement = requirementSelect.value;


        const orderRequestDto = {
            postNo: document.querySelector("input[name = 'postNo']").value,
            address: document.querySelector("input[name = 'address']").value,
            addressDetail: document.querySelector("input[name = 'addressDetail']").value,
            requirement: requirement,
            payMethod: document.querySelector("input[name = 'payMethod']").value,
            orderStatus: "NEW",
            totalPrice: total,
            detailDtoList: productArr
        }
        fetch("/api/order/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(orderRequestDto),
        })
            .then((response) => {
                if (!response.ok) {
                    alert("주문에 실패했습니다.");
                    throw new Error(`HTTP error! status: ${response.status}`);
                } else {
                    localStorage.removeItem("cart")
                    const modal = new bootstrap.Modal(document.getElementById("orderCompleteModal"));
                    modal.show();
                    return response.json();
                }
            })
            .then((data) => console.log("Response:", data))
            .catch((error) => console.error("Error:", error))

        console.log(orderRequestDto);
    }
}

document.getElementById("checkout-btn").addEventListener("click", checkout);
document.getElementById("total-count").innerText = productArr.length;
const requirementSelect = document.getElementById("requirement");
const requireTextarea = document.getElementById("message-textarea");
requirementSelect.addEventListener("change", () => {
    if (requirementSelect.value === "직접입력")
        requireTextarea.style.display = "block";
    else
        requireTextarea.style.display = "none";
})

renderProduct();