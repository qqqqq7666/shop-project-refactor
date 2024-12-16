function onRoleChange(selectElement) {
    const memberId = selectElement.getAttribute("data-id");
    const newRole = selectElement.value;
    const previousRole = selectElement.getAttribute("data-previous-value");

    if (confirm(`권한을 '${newRole}'(으)로 변경하시겠습니까?`)) {
        fetch(`/api/admin/members/role/${memberId}`, {
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