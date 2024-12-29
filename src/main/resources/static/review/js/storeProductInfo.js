// 세션 스토리지에 데이터 저장
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.review-btn').forEach(button => {
        button.addEventListener('click', function () {
            const productInfo = {
                orderDetailId: this.dataset.orderDetailId,
                productName: this.dataset.productName,
                color: this.dataset.productColor,
                size: this.dataset.productSize,
            };

            sessionStorage.setItem("productInfo", JSON.stringify(productInfo));

            window.location.href = '/review/create';
        });
    });
});

