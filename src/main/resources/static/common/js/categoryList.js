fetch('/api/v1/categories/categoryList', {
    method: 'GET'
})
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("문제가 발생했습니다. 다시 시도해주세요.");
        }
    })
    .then(data => {
        navigationBar(data);
    })
    .catch(error => {
        console.error("에러 발생:", error);
        alert('문제가 발생했습니다. 다시 시도해주세요.');
    });

function navigationBar(categories) {
    const navbar = document.querySelector('.navbar-nav');

    let categoryList = '';
    categories.forEach(category => {
        let subCategoryList = '';

        category.subCategories.forEach(subCategory => {
            subCategoryList += `
                <li>
                    <a class="dropdown-item" href="#" onclick="loadProductsByCategory(${subCategory.categoryId}, '${subCategory.categoryName}', false)">${subCategory.categoryName}</a>
                </li>
            `;
        });

        categoryList += `
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    ${category.categoryName}
                </a>
                <ul class="dropdown-menu">
                    ${subCategoryList}
                </ul>
            </li>
        `;
    });
    navbar.innerHTML = categoryList;
}

// 카테고리 표시 업데이트
const selectedCategoryText = document.getElementById("selectedCategoryText");

// 카테고리 이름 구성
if (mainCategoryName === "전체" || !mainCategoryName) {
    selectedCategoryText.textContent = "전체";
} else if (subCategoryName && subCategoryName !== "") {
    selectedCategoryText.textContent = `${mainCategoryName} ＞ ${subCategoryName}`;
} else {
    selectedCategoryText.textContent = mainCategoryName;
}

// 카테고리 클릭 시 상품 로드
function loadProductsByCategory(categoryId,  categoryName, isSubCategory = false) {
    console.log("Category ID:", categoryId);
    console.log("Category Name:", categoryName);
    console.log("Is Subcategory:", isSubCategory);

    const url = `/products/productList?categoryId=${categoryId}`;

    if (selectedCategoryText) {
        selectedCategoryText.textContent = isSubCategory
            ? `${mainCategoryName || "전체"} ＞ ${categoryName}`
            : categoryName;
    } else {
        console.error("selectedCategoryText 요소를 찾을 수 없습니다.");
    }

    window.location.href = url;
}