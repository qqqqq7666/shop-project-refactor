function deleteSubCategory(categoryId) {
    console.log("categoryId: " + categoryId);
    fetch('/api/v1/categories/delete', {
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
                return response.json();
            } else if (response.status === 404) {
                throw new Error('카테고리 삭제 실패');
            } else { // 그 외 에러 처리
                throw new Error("카테고리 삭제에 실패했습니다.");
            }
        })
        .then(data => {
            alert('카테고리가 삭제되었습니다.');
            deleteCategoryToUI(data);
        })
        .catch(error => {
            console.error("에러 발생:", error);
            alert('문제가 발생했습니다. 다시 시도해주세요.');
        });
}

function deleteCategoryToUI(data) {
    const subCategory = document.getElementById(`sub-category-${data.subCategoryId}`);
    const parentElement = subCategory.parentElement;

    parentElement.remove();

    console.log(data.existMainCategory === false);
    if(data.existMainCategory === false) {
        const mainCategory = document.getElementById(`main-category-box-${data.mainCategoryId}`);
        console.log(`main-category-box-${data.mainCategoryId}`);
        mainCategory.remove();
    }
}