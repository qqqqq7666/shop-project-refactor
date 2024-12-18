document.getElementById('addSizeColorStock').addEventListener('click', function () {
    const container = document.getElementById('sizeColorStockContainer');

    // 색상 및 재고 입력 필드 그룹 생성
    const div = document.createElement('div');
    div.classList.add('size-color-stock-group');
    div.innerHTML = `
    <select name="sizes[]" required>
      <option value="">사이즈 선택</option>
      <option value="S">S</option>
      <option value="M">M</option>
      <option value="L">L</option>
      <option value="XL">XL</option>
    </select>
    <input type="text" name="colors[]" placeholder="색상 입력" required>
    <input type="number" name="stocks[]" placeholder="재고 입력" min="0" required>
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

imageInput.addEventListener("change", () => {
    images = Array.from(imageInput.files).map((file, index) => ({
        file,
        order: index + 1, // 초기 순서는 업로드된 순서
    }));

    console.log("선택된 파일 목록:", images); // 파일 데이터 출력
    renderImagePreviews();
});

function renderImagePreviews() {
    imagePreviewList.innerHTML = ""; // 기존 미리보기 초기화

    images.forEach((image, index) => {
        const li = document.createElement("li");
        li.classList.add("image-item");
        li.setAttribute("data-index", index); // 인덱스 저장

        li.innerHTML = `
      <div class="image-container">
        <button class="drag-handle" draggable="true">☰</button>
        <img src="${URL.createObjectURL(image.file)}" class="image-preview">
        <span>순서: ${index + 1}</span>
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
        });

        imagePreviewList.appendChild(li);
    });
}

// 이미지 순서 바꾸기
function swapImages(fromIndex, toIndex) {
    const temp = images[fromIndex];
    images[fromIndex] = images[toIndex];
    images[toIndex] = temp;
}


// 폼 제출 처리
document.getElementById('productForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const images = document.getElementById('images').files;

    // 이미지 개수 제한 검사
    if (images.length > 5) {
        alert("*** 이미지는 최대 5개까지 저장 가능합니다 ***"); // 알림창 표시
        return; // 제출 중단
    }

    const formData = new FormData();

    // ProductRequestDto 생성
    const productRequestDto = {
        categoryName: document.getElementById('categoryName').value,
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
    for (let i = 0; i < images.length; i++) {
        formData.append('images', images[i]);
    }

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
            console.log(data);
        })
        .catch(errors => {
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
