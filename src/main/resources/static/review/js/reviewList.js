const reviews = document.querySelectorAll('.content-wrapper');

reviews.forEach(review => {
    const text = review.querySelector('.review-content');
    const moreText = review.querySelector('.more-text');
    const lessText = review.querySelector('.less-text');

    const lineHeight = parseInt(window.getComputedStyle(text).lineHeight, 10);
    const maxHeight = lineHeight * 3;

    if (text.scrollHeight > maxHeight) {
        moreText.style.display = 'inline-block'; // 3줄 이상일 경우 더보기 표시
    } else {
        moreText.style.display = 'none'; // 3줄 미만일 경우 더보기 숨기기
    }

    // 더보기 클릭 시
    moreText.addEventListener('click', () => {
        moreText.style.display = 'none'; // 더보기 삭제
        lessText.style.display = 'inline-block'; // 줄이기 표시
        text.style.display = 'inline-block'; // 텍스트의 속성을 -webkit-box에서 일반 inline-block 으로 변경
    });

    // 줄이기 클릭 시
    lessText.addEventListener('click', () => {
        lessText.style.display = 'none'; // 줄이기 삭제
        moreText.style.display = 'inline-block'; // 더보기 표시
        text.style.display = '-webkit-box'; // 텍스트의 속성을 다시 -webkit-box로 표시
    });
});

function changeSortOption() {
    const sortOption = document.getElementById('sortSelect').value;
    const currentUrl = window.location.href.split('?')[0]; // URL에서 쿼리스트링 제외
    window.location.href = `${currentUrl}?sort=${sortOption}&page=0`; // 페이지 0으로
}