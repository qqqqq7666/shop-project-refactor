function onRoleChange(selectElement) {
    const memberId = selectElement.getAttribute("data-id");
    const newRole = selectElement.value;
    const previousRole = selectElement.getAttribute("data-previous-value");

    if (confirm(`권한을 '${newRole}'(으)로 변경하시겠습니까?`)) {
        fetch(`/api/v1/admin/members/role/${memberId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ role: newRole })
        })
            .then(response => {
                if (response.ok) {
                    alert("권한이 성공적으로 변경되었습니다.");
                    selectElement.setAttribute("data-previous-value", newRole); // 변경된 값을 초기값으로 설정
                } else {
                    alert("권한 변경에 실패했습니다.");
                    selectElement.value = previousRole; // 실패 시 이전 값으로 복원
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("오류가 발생했습니다.");
                selectElement.value = previousRole; // 오류 발생 시 이전 값으로 복원
            });
    } else {
        selectElement.value = previousRole; // 취소 시 이전 값으로 복원
    }
}

// 회원 삭제버튼
document.querySelectorAll(".delete-member-btn").forEach(button => {
    button.addEventListener("click", function () {
        const memberId = this.getAttribute("data-id");

        if (confirm("탈퇴 처리하시겠습니까?")) {
            fetch(`/api/v1/admin/members/withdraw/${memberId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(response => {
                    if (response.ok) {
                        alert("회원이 탈퇴 처리되었습니다.");
                        location.reload(); // 삭제된 상태를 반영하기 위해 페이지 새로고침
                    } else {
                        alert("회원 탈퇴 처리에 실패했습니다.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("오류가 발생했습니다.");
                });
        }
    });
});