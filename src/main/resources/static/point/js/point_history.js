function showTab(tabId) {
    // 모든 탭 내용 숨기기
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    // 선택된 탭 내용 표시
    document.getElementById(tabId).classList.add('active');

    // 모든 탭 버튼의 active 제거
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });

    // 선택된 탭 버튼에 active 추가
    event.target.classList.add('active');
}

const taps = document.getElementsByClassName("nav-tabs");
