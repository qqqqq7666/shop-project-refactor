function openPostcodePopup() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 우편번호 데이터 입력
            document.getElementById('postNo').value = data.zonecode; // 우편번호

            // 주소 선택 옵션 제공
            const addressInput = document.getElementById('address');
            const roadAddress = data.roadAddress; // 도로명 주소
            const jibunAddress = data.jibunAddress; // 지번 주소

			if (roadAddress) {
			    addressInput.value = roadAddress; // 기본적으로 도로명 주소를 사용
			} else if (jibunAddress) {
			    addressInput.value = jibunAddress; // 도로명 주소가 없으면 지번 주소 사용
			} else {
			    addressInput.value = ''; // 둘 다 없으면 초기화
			    alert('주소를 찾을 수 없습니다. 다시 시도해주세요.');
			}

        }
    }).open();
}