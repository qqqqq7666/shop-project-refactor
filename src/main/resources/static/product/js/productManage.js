function editProduct(productId) {
    window.location.href = '/products/edit/' + productId;
}

function performSearch() {
    const searchInput = document.querySelector('input[name="search"]').value || '';
    const url = `/admin/products?keyword=${encodeURIComponent(searchInput)}`;
    window.location.href = url; // 페이지 이동
}


function submitPartialUpdate() {
    const productId = document.getElementById("productId").value;

    // 수정할 데이터 수집
    const updates = {};
    const categoryName = document.getElementById('categoryName').value;
    const productName = document.getElementById('productName').value;
    const description = document.getElementById('description').value;
    const price = document.getElementById('price').value;

    if (categoryName) updates.categoryName = categoryName;
    if (productName) updates.productName = productName;
    if (description) updates.description = description;
    if (price) updates.price = parseInt(price, 10);

    // 옵션 데이터 수집
    updates.options = [];
    const colorInputs = document.querySelectorAll('input[name="colors[]"]');
    const stockInputs = document.querySelectorAll('input[name="stocks[]"]');
    const sizeInputs = document.querySelectorAll('select[name="sizes[]"]');

    for (let i = 0; i < colorInputs.length; i++) {
        updates.options.push({
            size: sizeInputs[i].value,
            color: colorInputs[i].value,
            stockQuantity: parseInt(stockInputs[i].value, 10),
        });
    }

    // FormData 객체 생성
    const formData = new FormData();
    formData.append('updates', JSON.stringify(updates)); // JSON 문자열로 추가

    // 기존 이미지 URL 수집
    const existingImages = document.querySelectorAll('.prevImages');
    existingImages.forEach((image, index) => {
        // 이미지 URL을 FormData에 추가
        formData.append(`existingImageUrls`, image.src);
    });

    // 이미지 추가
    const imageInput = document.getElementById("images");
    if (imageInput.files.length > 0) {
        for (const file of imageInput.files) {
            formData.append("images", file);
        }
    }

    // PATCH 요청 전송
    fetch(`/api/products/${productId}`, {
        method: "PATCH",
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error("수정 실패");
            return response.json();
        })
        .then(data => {
            alert("상품이 성공적으로 수정되었습니다!");
            window.location.href = "/admin/products";
        })
        .catch(error => {
            console.error("Error updating product:", error);
            alert("수정 중 오류가 발생했습니다.");
        });
}


function deleteProduct(productId) {
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch('/api/products/' + productId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(response => {
            if (response.ok) {
                alert("삭제되었습니다.");
                window.location.reload();
            } else {
                alert("삭제에 실패했습니다.");
            }
        }).catch(error => {
            console.error("Error:", error);
            alert("오류가 발생했습니다.");
        });
    }
}
