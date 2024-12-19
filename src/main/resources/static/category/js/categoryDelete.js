function deleteSubCategory(categoryId) {
    console.log("categoryId: " + categoryId);
    fetch('/admin/category/delete', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            categoryId: categoryId,
        })
    })
        .then(response => {
            if (response.ok) {
                console.log("삭제 성공");
                alert("성공적으로 삭제되었습니다!");
                location.reload(); // 페이지를 새로고침하여 목록 갱신
            } else if (response.status === 400) {  // badRequest
                throw new Error('카테고리 삭제 실패');
            } else if (response.status === 404) {  //
                throw new Error('카테고리 삭제 실패');
            } else {
                throw new Error("요청 실패"); // 그 외 에러 처리
            }
        })
        .catch(error => {
            console.error("에러 발생:", error);
            alert('문제가 발생했습니다. 다시 시도해주세요.');
        });
}