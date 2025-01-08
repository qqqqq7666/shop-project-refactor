function editProduct(productId) {
    window.location.href = '/products/edit/' + productId;
}

// 메인 카테고리 선택 시 서브 카테고리 불러오기
document.getElementById("mainCategory").addEventListener("change", function () {
    const mainCategoryId = this.value;
    const subCategorySelect = document.getElementById("subCategory");

    if (!mainCategoryId) {
        subCategorySelect.innerHTML = '<option value="">서브 카테고리를 선택하세요</option>';
        return;
    }

    // 서버에서 서브 카테고리 가져오기
    fetch(`/categories/${mainCategoryId}/sub`)
        .then(response => response.json())
        .then(data => {
            // 서브 카테고리 옵션 초기화
            subCategorySelect.innerHTML = '<option value="">서브 카테고리를 선택하세요</option>';
            data.forEach(subCategory => {
                const option = document.createElement("option");
                option.value = subCategory.categoryId;
                option.textContent = subCategory.categoryName;
                subCategorySelect.appendChild(option);
            });
        })
        .catch(error => console.error("Error loading subcategories:", error));
});



document.getElementById('addSizeColorStock').addEventListener('click', function () {
    const container = document.getElementById('sizeColorStockContainer');

    // 색상 및 재고 입력 필드 그룹 생성
    const div = document.createElement('div');
    div.classList.add('size-color-stock-group');
    div.innerHTML = `
    <select class="form-control" name="sizes[]" required>
      <option value="">사이즈 선택</option>
      <option value="S">S</option>
      <option value="M">M</option>
      <option value="L">L</option>
      <option value="XL">XL</option>
    </select>
    <input class="form-control" type="text" name="colors[]" placeholder="색상 입력" required>
    <input class="form-control" type="number" name="stocks[]" placeholder="재고 입력" min="0" required>
    <button type="button" class="remove-size-color-stock">삭제</button>
  `;

    // 그룹을 컨테이너에 추가
    container.appendChild(div);

    // 삭제 버튼에 이벤트 리스너 추가
    div.querySelector('.remove-size-color-stock').addEventListener('click', function () {
        container.removeChild(div);
    });
});

// 이미지 데이터
let existingImages = []; // 기존 이미지 URL
let newImages = []; // 새로 추가된 이미지 파일
const imagePreviewList = document.getElementById("imagePreviewList");
const imageInput = document.getElementById("images");
const customFileButton = document.getElementById("customFileButton");
const fileCountMessage = document.getElementById("fileCountMessage");

console.log("imageInput:", imageInput);
console.log("customFileButton:", customFileButton);

let allImages = []; // 모든 이미지 (기존 + 새로 추가)

// DOMContentLoaded에서 이미지 불러오기
document.addEventListener("DOMContentLoaded", () => {
    const productId = document.getElementById("productId").value; // 상품 ID 가져오기
    fetch(`/products/edit/${productId}/images`)
        .then(response => response.json())
        .then(async (data) => {
            // 서버에서 받은 기존 이미지 URL 배열을 저장
            allImages = await Promise.all(
                data.map(async (url, index) => {
                    const file = await urlToFile(url); // URL을 파일로 변환
                    return {
                        url,           // URL 정보
                        file,          // 파일 정보
                        type: "existing", // 기존 이미지 표시
                        order: index + 1, // 순서
                    };
                })
            );

            console.log("기존 이미지 All Images:", allImages);
            renderAllImages(); // 모든 이미지 렌더링
        })
        .catch(error => {
            console.error("Error loading existing images:", error);
            alert("기존 이미지를 불러오는 중 오류가 발생했습니다.");
        });
});

// URL을 파일로 변환하는 함수
async function urlToFile(url) {
    const response = await fetch(url);
    const blob = await response.blob();
    const fileName = url.split('/').pop(); // URL에서 파일 이름 추출
    return new File([blob], fileName, { type: blob.type });
}

// 이미지 선택 버튼 이벤트 리스너
customFileButton.addEventListener("click", () => {
    imageInput.click(); // 실제 파일 선택 창 열기
});

// 이미지 파일 변경 이벤트
imageInput.addEventListener("change", async function () {
    const files = Array.from(imageInput.files);

    // 현재 이미지 개수 + 새로 추가된 파일 개수 확인
    if (allImages.length + files.length > 5) {
        alert("*** 이미지는 최대 5개까지 저장 가능합니다 ***");
        imageInput.value = ""; // 입력값 초기화
        return;
    }

    for (const file of files) {
        console.log("파일 처리 시작");
        if (file.type.startsWith("image/")) {
            try {
                // 이미지 압축 처리
                const compressedFile = await compressImage(file, 0.8, 800); // 압축 실행
                allImages.push({ file: compressedFile, type: "new", order: allImages.length + 1 }); // 압축된 파일 추가
                console.log("압축된 파일 추가 완료");
            } catch (error) {
                console.error("이미지 압축 실패:", error);
            }
        } else {
            images.push({ file, order: images.length + 1 }); // 이미지가 아닌 경우 원본 파일 추가
            console.log("원본 파일 추가 완료");
        }
    }

    console.log("이미지 추가 했을때 All Images:", allImages);

    renderAllImages(); // 모든 이미지 렌더링
    updateFileCount(); // 파일 개수 업데이트
});

// 모든 이미지 렌더링
function renderAllImages() {
    imagePreviewList.innerHTML = ""; // 초기화

    allImages
        .sort((a, b) => a.order - b.order) // 순서 기준 정렬
        .forEach((image, index) => {
            const li = document.createElement("li");
            li.classList.add("image-item");
            if (index === 0) {
                li.classList.add("highlight"); // 첫 번째 이미지에만 highlight 클래스 추가
            }
            li.setAttribute("data-index", index); // 인덱스 저장

            li.innerHTML = `
                <div class="image-container">
                    <button class="drag-handle" draggable="true">☰</button>
                    ${image.type === "existing" ?
                `<img src="${image.url}" alt="Existing Image" class="image-preview">` :
                `<img src="${URL.createObjectURL(image.file)}" alt="New Image" class="image-preview">`}
                    <span>순서: ${index + 1}</span>
                    <button type="button" class="delete-button" onclick="removeImage(${index})">x</button>
                </div>
            `;

            // 드래그 앤 드롭 이벤트 추가
            const handle = li.querySelector(".drag-handle");

            handle.addEventListener("dragstart", (e) => {
                dragStartIndex = index;
                e.dataTransfer.effectAllowed = "move";
                e.dataTransfer.setData("text/plain", dragStartIndex);
            });

            li.addEventListener("dragover", (e) => {
                e.preventDefault();
                li.classList.add("drag-over"); // 시각적 효과
            });

            li.addEventListener("dragleave", () => {
                li.classList.remove("drag-over");
            });

            li.addEventListener("drop", (e) => {
                e.preventDefault();
                li.classList.remove("drag-over");

                const dragEndIndex = parseInt(li.getAttribute("data-index"), 10);
                swapImages(dragStartIndex, dragEndIndex);
                renderAllImages();
            });

            imagePreviewList.appendChild(li);
        });
    updateFileCount(); // 파일 개수 업데이트
}

// 이미지 순서 바꾸기
function swapImages(fromIndex, toIndex) {
    const temp = allImages[fromIndex];
    allImages[fromIndex] = allImages[toIndex];
    allImages[toIndex] = temp;

    // 순서 업데이트
    allImages.forEach((image, index) => {
        image.order = index + 1;
    });
}

// 이미지 삭제
function removeImage(index) {
    allImages.splice(index, 1);
    renderAllImages();
}

// 파일 개수 업데이트
function updateFileCount() {
    fileCountMessage.textContent = `선택된 파일: ${allImages.length}개`;
}

function compressImage(file, quality = 0.8, maxWidth = 800) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);

        reader.onload = (event) => {
            const img = new Image();
            img.src = event.target.result;

            img.onload = () => {
                const canvas = document.createElement("canvas");
                const ctx = canvas.getContext("2d");

                // 이미지 크기 조정
                let width = img.width;
                let height = img.height;

                if (width > maxWidth) {
                    height = (maxWidth / width) * height;
                    width = maxWidth;
                }

                canvas.width = width;
                canvas.height = height;
                ctx.drawImage(img, 0, 0, width, height);

                // 압축된 데이터 생성
                canvas.toBlob(
                    (blob) => {
                        if (blob) {
                            resolve(new File([blob], file.name, { type: blob.type }));
                        } else {
                            reject(new Error("이미지 압축 실패"));
                        }
                    },
                    file.type,
                    quality
                );
            };

            img.onerror = () => reject(new Error("이미지 로드 실패"));
        };

        reader.onerror = () => reject(new Error("파일 읽기 실패"));
    });
}


// 수정 API 호출
function submitPartialUpdate() {
    const productId = document.getElementById("productId").value;

    // 수정할 데이터 수집
    const updates = {};
    const categoryId = document.getElementById("subCategory").value;
    const productName = document.getElementById("productName").value;
    const description = document.getElementById("description").value;
    const price = document.getElementById("price").value;

    if (categoryId) updates.categoryId = categoryId;
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

    // FormData 생성
    const formData = new FormData();
    formData.append('updates', JSON.stringify(updates)); // JSON 추가

    // 새로운 이미지 파일 추가
    allImages.forEach(image => {
        formData.append("images", image.file);
    });

    // PATCH 요청
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

// 초기 렌더링
renderAllImages();