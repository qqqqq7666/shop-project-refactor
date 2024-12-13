function openPostcodePopup() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 우편번호 데이터 입력
            document.getElementById('postNo').value = data.zonecode; // 우편번호

            // 주소 선택 옵션 제공
            const addressInput = document.getElementById('address');
            const roadAddress = data.roadAddress; // 도로명 주소
            const jibunAddress = data.jibunAddress; // 지번 주소

            // 도로명 주소를 선택했을 경우
            if (roadAddress) {
                addressInput.value = roadAddress; // 도로명 주소를 address 필드에 넣음
            }

            // 지번 주소를 선택했을 경우
            if (jibunAddress) {
                addressInput.value = jibunAddress; // 지번 주소를 address 필드에 넣음
            }
        }
    }).open();
}