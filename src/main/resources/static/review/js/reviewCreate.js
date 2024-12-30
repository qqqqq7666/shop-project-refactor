// 폼 제출 시
document.getElementById('review-form').addEventListener('submit', function (event) {
    let isValid = true;

    // 별점 유효성 검사
    const stars = document.querySelectorAll('input[name="stars"]:checked');
    const starMessage = document.getElementById('star-message');
    if (stars.length === 0) { // 별점 체크 X (빈 NodList)
        starMessage.classList.remove('d-none'); // 에러 메세지 표시
        isValid = false;
    } else {
        starMessage.classList.add('d-none'); // 에러 메세지 숨김
    }

    // 내용 유효성 검사
    const reviewContent = document.getElementById('review-contents');
    const currentCount = document.getElementById('current-count');
    const contentError = document.getElementById('content-error');
    const contentLength = reviewContent.value.trim().length;

    currentCount.textContent = contentLength; // 현재 글자 수

    if (contentLength < 20 || contentLength > 500) {
        contentError.classList.remove('d-none'); // 에러 메세지 표시
        isValid = false;
    } else {
        contentError.classList.add('d-none'); // 에러 메세지 숨김
    }

    // 유효하지 않을 경우 폼 제출 막음
    if (!isValid) {
        event.preventDefault();
    }
});

// 실시간 글자 수 업데이트
document.getElementById('review-contents').addEventListener('input', function () {
    const reviewContent = document.getElementById('review-contents');
    const currentCount = document.getElementById('current-count');
    const contentError = document.getElementById('content-error');
    const contentLength = reviewContent.value.trim().length;

    currentCount.textContent = contentLength; // 현재 글자 수

    if (contentLength < 20 || contentLength > 500) {
        contentError.classList.remove('d-none');
    } else {
        contentError.classList.add('d-none');
    }
});