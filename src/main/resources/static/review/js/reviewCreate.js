document.getElementById("review-form").addEventListener("submit", function(event) {
    const stars = document.querySelector('input[name="stars"]:checked');
    if (!stars) {
        const starMessage = document.getElementById("star-message");
        starMessage.style.color = "red";
        event.preventDefault();
    } else {
        starMessage.style.color = "black";
    }
});

// 별점 선택하면 경고 메시지 초기화
document.querySelectorAll('input[name="stars"]').forEach(function(radio) {
    radio.addEventListener("change", function() {
        const starMessage = document.getElementById("star-message");
        starMessage.style.color = "black";
    });
});