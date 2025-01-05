document.addEventListener('DOMContentLoaded', () => {
    const slides = document.querySelectorAll('.slide');
    let currentSlide = 0;

    function showNextSlide() {
        slides[currentSlide].classList.remove('active'); // 현재 슬라이드 숨기기
        currentSlide = (currentSlide + 1) % slides.length; // 다음 슬라이드 인덱스 계산
        slides[currentSlide].classList.add('active'); // 다음 슬라이드 표시
    }

    setInterval(showNextSlide, 5000); // 2초마다 슬라이드 변경
});
