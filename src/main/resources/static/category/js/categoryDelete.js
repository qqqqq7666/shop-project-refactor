function deleteSubCategory(categoryId) {
    console.log("categoryId: " + categoryId);
    const isConfirmed = confirm("해당 카테고리를 삭제하시겠습니까?");
    if (!isConfirmed) {
        return;
    }

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
            } else if (response.status === 409) { // 해당 카테고리를 가지고 있는 상품 존재
                return response.json().then(errorMessage => {throw errorMessage});
            } else { // 그 외 에러 처리
                throw new Error("카테고리 삭제에 실패했습니다.");
            }
        })
        .then(data => {
            alert('카테고리가 삭제되었습니다.');
            deleteCategoryToUI(data);
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message);
        });
}

function deleteCategoryToUI(data) {
    const subCategory = document.getElementById(`sub-category-${data.subCategoryId}`);
    const tdTag = subCategory.parentElement;
    const trTag = tdTag.parentElement;

    trTag.remove();

    console.log(data.existMainCategory === false);
    if(data.existMainCategory === false) {
        const mainCategory = document.getElementById(`main-category-box-${data.mainCategoryId}`);
        console.log(`main-category-box-${data.mainCategoryId}`);
        mainCategory.remove();
    }
}