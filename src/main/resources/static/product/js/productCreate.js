document.addEventListener("DOMContentLoaded", () => {
    const mainCategorySelect = document.getElementById("mainCategory");
    const subCategorySelect = document.getElementById("subCategory");

    // 서버에서 카테고리 데이터 가져오기
    fetch("/api/v1/categories/list")
        .then((response) => response.json())
        .then((categories) => {
            // 메인 카테고리 추가
            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.categoryId;
                option.textContent = category.categoryName;
                mainCategorySelect.appendChild(option);
            });

            // 메인 카테고리 선택 시 서브 카테고리 필터링
            mainCategorySelect.addEventListener("change", () => {
                const selectedCategoryId = parseInt(mainCategorySelect.value);
                subCategorySelect.innerHTML = `<option value="">서브 카테고리를 선택하세요</option>`;
                subCategorySelect.disabled = true;

                if (selectedCategoryId) {
                    // 선택된 메인 카테고리의 서브 카테고리 추가
                    const selectedCategory = categories.find(
                        (category) => category.categoryId === selectedCategoryId
                    );
                    if (selectedCategory && selectedCategory.subCategories) {
                        selectedCategory.subCategories.forEach((subCategory) => {
                            const option = document.createElement("option");
                            option.value = subCategory.categoryId;
                            option.textContent = subCategory.categoryName;
                            subCategorySelect.appendChild(option);
                        });

                        subCategorySelect.disabled = false;
                    }
                }
            });
        })
        .catch((error) => {
            console.error("Error loading categories:", error);
            alert("카테고리를 불러오는 중 오류가 발생했습니다.");
        });
});


document.getElementById('addSizeColorStock').addEventListener('click', function () {
    const container = document.getElementById('sizeColorStockContainer');

    // 색상 및 재고 입력 필드 그룹 생성
    const div = document.createElement('div');
    div.classList.add('size-color-stock-group');
    div.innerHTML = `
    <select name="sizes[]" required class="form-control">
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

const imageInput = document.getElementById("images");
const imagePreviewList = document.getElementById("imagePreviewList");
let images = []; // 이미지 정보 저장 (file과 order)

const customFileButton = document.getElementById("customFileButton");
const fileCountMessage = document.getElementById("fileCountMessage");

customFileButton.addEventListener("click", () => {
    console.log("파일 선택 버튼 클릭");
    imageInput.click(); // 실제 파일 선택 필드 클릭
});

imageInput.addEventListener("change", async function (event) {
    const files = Array.from(imageInput.files);
    console.log("선택된 파일 목록:", images); // 파일 데이터 출력

    // 현재 이미지 개수 + 새로 추가된 파일 개수 확인
    if (images.length + files.length > 5) {
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
                images.push({ file: compressedFile, order: images.length + 1 }); // 압축된 파일 추가
                console.log("압축된 파일 추가 완료");
            } catch (error) {
                console.error("이미지 압축 실패:", error);
            }
        } else {
            images.push({ file, order: images.length + 1 }); // 이미지가 아닌 경우 원본 파일 추가
            console.log("원본 파일 추가 완료");
        }
    }

    // 이미지 미리보기 렌더링
    renderImagePreviews();
    console.log("최종 선택된 파일 목록:", images);

    // 파일 개수 표시
    fileCountMessage.textContent = `선택된 파일: ${images.length}개`;

    // 입력값 초기화로 같은 파일 재선택 가능하도록 설정
    imageInput.value = "";
});

function renderImagePreviews() {
    imagePreviewList.innerHTML = ""; // 기존 미리보기 초기화

    images.forEach((image, index) => {
        const li = document.createElement("li");
        li.classList.add("image-item");
        if (index === 0) {
            li.classList.add("highlight"); // 첫 번째 이미지에만 highlight 클래스 추가
        }
        li.setAttribute("data-index", index); // 인덱스 저장

        li.innerHTML = `
      <div class="image-container">
        <button class="drag-handle" draggable="true">☰</button>
        <img src="${URL.createObjectURL(image.file)}" class="image-preview">
        <span>순서: ${index + 1}</span>
        <button class="delete-button" type="button" onclick="removeImage(${index})">x</button>
      </div>
    `;

        const handle = li.querySelector(".drag-handle");

        // 드래그 시작 이벤트 핸들러 (핸들 버튼에만 적용)
        handle.addEventListener("dragstart", (e) => {
            dragStartIndex = parseInt(li.getAttribute("data-index"), 10);
            e.dataTransfer.effectAllowed = "move";
            e.dataTransfer.setData("text/plain", dragStartIndex);
        });

        // 드래그 오버 이벤트 핸들러
        li.addEventListener("dragover", (e) => {
            e.preventDefault();
            li.classList.add("drag-over"); // 시각적 효과
        });

        // 드래그 떠날 때 시각 효과 제거
        li.addEventListener("dragleave", () => {
            li.classList.remove("drag-over");
        });

        // 드롭 이벤트 핸들러
        li.addEventListener("drop", (e) => {
            e.preventDefault();
            li.classList.remove("drag-over");

            const dragEndIndex = parseInt(li.getAttribute("data-index"), 10);
            swapImages(dragStartIndex, dragEndIndex);
            renderImagePreviews();
            console.log("최종 이미지 순서:", images);
        });

        imagePreviewList.appendChild(li);
    });
}

// 이미지 순서 바꾸기
function swapImages(fromIndex, toIndex) {
    const temp = images[fromIndex];
    images[fromIndex] = images[toIndex];
    images[toIndex] = temp;

    images.forEach((image, index) => {
        image.order = index + 1; // 인덱스 기반으로 order를 재설정
    });
}

// 이미지 삭제 함수
function removeImage(index) {
    images.splice(index, 1); // 해당 인덱스 이미지 삭제
    images.forEach((image, i) => (image.order = i + 1)); // order 재정렬
    fileCountMessage.textContent = `선택된 파일: ${images.length}개`;
    renderImagePreviews();
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


// 폼 제출 처리
document.getElementById('productForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const formData = new FormData();

    if(images.length === 0) {
        fileCountMessage.textContent = '최소 1개의 이미지를 선택해야 합니다.';
        fileCountMessage.style.color = 'red';
    }


    // ProductRequestDto 생성
    const productRequestDto = {
        categoryId: document.getElementById('subCategory').value, // 선택한 서브 카테고리 ID
        productName: document.getElementById('productName').value,
        description: document.getElementById('description').value,
        price : document.getElementById('price').value,
        options: [] // 옵션 리스트
    };

    // 옵션 데이터를 수집
    const colorInputs = document.querySelectorAll('input[name="colors[]"]');
    const stockInputs = document.querySelectorAll('input[name="stocks[]"]');
    const sizeInputs = document.querySelectorAll('select[name="sizes[]"]');

    for (let i = 0; i < colorInputs.length; i++) {
        const color = colorInputs[i].value;
        const stockQuantity = stockInputs[i].value;
        const size = sizeInputs[i].value;

        productRequestDto.options.push({
            size: size,
            color: color,
            stockQuantity: parseInt(stockQuantity, 10)
        });
    }

    // JSON 데이터를 FormData에 추가
    formData.append(
        'productRequestDto',
        new Blob([JSON.stringify(productRequestDto)], { type: 'application/json' })
    );

    // 이미지 파일 추가
    images.forEach(image => {
        formData.append('images', image.file);
    });

    // REST API 호출
    fetch('/api/products', {
        method: 'POST',
        body: formData,
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errors => {
                    throw errors; // 서버에서 받은 에러 메시지를 예외로 던짐
                });
            }
            return response.json();
        })
        .then(data => {
            alert("상품 등록이 성공적으로 완료되었습니다.");
            console.log("상품 등록 성공:", data);
            window.location.href = '/admin/products';
        })
        .catch(errors => {
            alert("상품 등록 중 문제가 발생했습니다.", errors);
            // 필드별 에러 메시지 표시
            Object.keys(errors).forEach(field => {
                const errorElement = document.getElementById(`${field}Error`);
                if (errorElement) {
                    errorElement.textContent = errors[field];
                }
            });
            console.error(errors);
        });
});
