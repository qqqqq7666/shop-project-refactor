function editProduct(productId) {
    // 수정 페이지로 이동
    window.location.href = `/admin/products/edit/${productId}`;
}

function deleteProduct(productId) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        fetch(`/api/products/${productId}`, {
            method: "DELETE",
        })
            .then(response => {
                if (response.ok) {
                    alert("삭제되었습니다.");
                    window.location.reload();
                } else {
                    alert("삭제에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("서버 오류가 발생했습니다.");
            });
    }
}
