// 세션 스토리지에서 데이터 가져오기
const productInfoDiv = document.querySelector('.product-info');
const reviewForm = document.querySelector('#review-form');

const productInfo = JSON.parse(sessionStorage.getItem("productInfo"));

if (productInfo) {
    productInfoDiv.innerHTML = `
            <p id="product-name">${productInfo.productName}</p>
            <p id="color-and-size">${productInfo.color} / ${productInfo.size}</p>
        `;

    const inputElement = document.createElement('input');
    inputElement.type = 'hidden';
    inputElement.name = 'orderDetailId';
    inputElement.value = productInfo.orderDetailId;

    reviewForm.appendChild(inputElement);
}