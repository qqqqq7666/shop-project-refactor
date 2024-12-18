let currentPage = 0;
let totalPages = 1;

// 서버에서 상품 목록 데이터를 가져옴
function fetchProducts(sortBy = "salesCount", page = 0) {
    fetch(`/api/products?sortBy=${sortBy}&page=${page}&size=10`)
        .then(response => response.json())
        .then(data => {
            totalPages = data.totalPages; // 총 페이지 수 업데이트
            renderProducts(data.content); // 상품 목록 렌더링
            updatePagination(); // 페이지네이션 업데이트
        })
        .catch(error => console.error("상품 목록 불러오기 오류:", error));
}

// 상품 목록을 화면에 렌더링
function renderProducts(products) {
    const productList = document.getElementById("product-list");
    productList.innerHTML = ""; // 기존 목록 초기화

    products.forEach(product => {
        const productCard = document.createElement("div");
        productCard.className = "product-card";

        productCard.innerHTML = `
            <img src="/${product.imageUrls[0] || 'default-image.jpg'}" alt="${product.productName}">
            <h3>${product.productName}</h3>
            <p>가격: ${product.price}원</p>
            <p>판매량: ${product.salesCount}</p>
            <p>조회수: ${product.viewCount}</p>
        `;

        productList.appendChild(productCard);
    });
}

// 페이지네이션 업데이트
function updatePagination() {
    const pageInfo = document.getElementById("pageInfo");
    pageInfo.textContent = `${currentPage + 1} / ${totalPages}`;

    // 이전/다음 버튼 활성화/비활성화
    document.getElementById("prevPage").disabled = currentPage === 0;
    document.getElementById("nextPage").disabled = currentPage === totalPages - 1;
}

// 정렬 기준 변경 시 처리
document.getElementById("sortBy").addEventListener("change", (event) => {
    const sortBy = event.target.value;
    currentPage = 0; // 정렬 변경 시 첫 페이지로 이동
    fetchProducts(sortBy, currentPage);
});

// 이전 페이지 버튼 클릭
document.getElementById("prevPage").addEventListener("click", () => {
    if (currentPage > 0) {
        currentPage--;
        fetchProducts(document.getElementById("sortBy").value, currentPage);
    }
});

// 다음 페이지 버튼 클릭
document.getElementById("nextPage").addEventListener("click", () => {
    if (currentPage < totalPages - 1) {
        currentPage++;
        fetchProducts(document.getElementById("sortBy").value, currentPage);
    }
});

// 페이지 로드 시 초기 데이터 가져오기
fetchProducts();
