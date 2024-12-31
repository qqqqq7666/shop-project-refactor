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
                    <a class="dropdown-item" href="#" onclick="loadProductsByCategory(${subCategory.categoryId})">${subCategory.categoryName}</a>
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

// 카테고리 클릭 시 상품 로드
function loadProductsByCategory(categoryId) {
    const url = `/products/productList?categoryId=${categoryId}`;
    window.location.href = url;
}