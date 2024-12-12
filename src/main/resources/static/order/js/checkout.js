// Example starter JavaScript for disabling form submissions if there are invalid fields
(() => {
  'use strict'

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  const forms = document.querySelectorAll('.needs-validation')

  // Loop over them and prevent submission
  Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      if (!form.checkValidity()) {
        event.preventDefault()
        event.stopPropagation()
      }

      form.classList.add('was-validated')
    }, false)
  })
})()

const cartList = document.getElementById("cart-list");
const productArr = JSON.parse(localStorage.getItem("cart"));
let total = 0;

for(i = 0; i < productArr.length; i++){
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
  product.getElementsByClassName("product-price")[0].innerText = `${price}원`;
  product.getElementsByClassName("product-quantity")[0].innerText = `x${quantity}`;

  cartList.prepend(product);
  total += price * quantity;
}

document.getElementById("total").innerText = total;