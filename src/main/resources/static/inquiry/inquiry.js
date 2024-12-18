let inquiries = []; // 현재 상품의 문의 데이터

// 문의 리스트 렌더링
function renderInquiries() {
    const questionList = document.getElementById("question-list");
    questionList.innerHTML = ""; // 기존 내용을 초기화

    inquiries.forEach((inquiry) => {
        const questionDiv = document.createElement("div");
        questionDiv.className = "question-item";
        questionDiv.innerHTML = `
            <div class="title">${inquiry.title}</div>
            <div class="content">${inquiry.content}</div>
            <div class="meta">
                작성자: ${inquiry.nickname} | 작성일: ${inquiry.date}
            </div>
        `;

        questionList.appendChild(questionDiv);
    });
}

// 문의 작성 폼 처리
document.getElementById("question-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const nickname = document.getElementById("nickname").value;
    const title = document.getElementById("question-title").value;
    const content = document.getElementById("question-content").value;

    const newInquiry = {
        id: Date.now(), // 임시 고유 ID
        nickname,
        title,
        content,
        date: new Date().toLocaleDateString(),
    };

    inquiries.push(newInquiry); // 로컬 데이터에 추가
    renderInquiries(); // 화면 갱신

    // 폼 초기화
    document.getElementById("question-form").reset();
});
