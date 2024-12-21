// Example starter JavaScript for disabling form submissions if there are invalid fields
let formChecked = false;
let requirement;
let deliveryFlag = document.querySelector("select[name = 'deliveryFlag']");

const channelKeyArray = [
    "channel-key-a085e15a-a36f-4d9c-88c3-de5c8958e389", // KG
    "channel-key-c94b2cb4-f67f-41de-b88b-5b2f281a9a1e", // 카카오
    "channel-key-5543f681-87e6-4bc7-9fc9-e93aaf0159ca"  // 토스
];
const payMethodArray = [
    "KG",
    "KAKAO",
    "TOSS"
];

// 유효성 검사
(() => {
    'use strict'

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const checkoutButton = document.getElementById("checkout-btn");

    checkoutButton.addEventListener("click", event => {
        const forms = document.querySelectorAll('.needs-validation');
        formChecked = true;
        Array.from(forms).forEach(form => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            formChecked &&= form.checkValidity();
            console.log("form.checkValidity() = " + form.checkValidity());

            form.classList.add('was-validated');
        })
        console.log("formChecked = " + formChecked);
    })

    if (localStorage.getItem("cart") === "[]" || localStorage.getItem("cart") === null) {
        alert("잘못된 접근입니다.");
        window.location.href = "/";    // 홈으로 바꿀 예정
    }

})();

function sample6_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.

            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('sample6_postcode').value = data.zonecode;
            document.getElementById("sample6_address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("sample6_detailAddress").focus();
        }
    }).open();
}

const cartList = document.getElementById("cart-list");
const cart = JSON.parse(localStorage.getItem("cart"));
let total = 0;

// 결제 품목 표시
function renderProduct() {

    for (i = 0; i < cart.length; i++) {
        const product = document.createElement("li");
        product.classList.add("list-group-item", "d-flex", "justify-content-between", "lh-sm");
        product.innerHTML = '<div>\n' +
            '              <h6 class="my-0 product-name">Product name</h6>' +
            '              <div class="product-detail justify-content-between"><br></div>' +
            // '              \n' +
            '            </div>\n' +
            '            <div class="text-body-secondary product-price"><br><br></div>';
        const productDetail = product.getElementsByClassName("product-detail")[0];
        const name = cart[i].name;
        const price = cart[i].price;
        let totalPrice = 0;
        cart[i].option.forEach(option => {
            const detail = document.createElement("div");
            const detailPrice = document.createElement("div");
            detail.innerHTML = `<small class='text-body-secondary'>(${option.size} / ${option.color}) x${option.quantity}</small>`;
            detailPrice.innerHTML = `<small class='text-body-secondary'>${(option.quantity * price).toLocaleString()}원</small>`;
            productDetail.appendChild(detail);
            product.getElementsByClassName("product-price")[0].appendChild(detailPrice);

            //
            totalPrice += option.quantity * price;
        })



        product.getElementsByClassName("product-name")[0].innerText = name;

        product.getElementsByClassName("product-price")[0].prepend(`${totalPrice.toLocaleString()}원`);
        cartList.prepend(product);
        total += totalPrice;
    }
    if (total >= 50000) {
        const discount = document.getElementById("discount");
        discount.style.removeProperty("display");
    } else
        total += 3000;

    document.getElementById("total").innerText = `${total.toLocaleString()}원`;
}

function requestPay(payInfo, paymentDto){
    IMP.init("imp31477127");
    IMP.request_pay(payInfo,
        async function (rsp) {
            // callback 로직
            if (rsp.success) {
                // payment create post 요청
                fetch("/api/payment/create", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(paymentDto),
                })
                    .then(res => {
                        if (res.ok) {
                            console.log("결제 데이터 저장 성공");
                            localStorage.removeItem("cart")
                            const modal = new bootstrap.Modal(document.getElementById("orderCompleteModal"));
                            modal.show();
                            return res.json();
                        }
                    })
            } else {
                const orderNoJson = await fetch("/api/order/recent-order-no");
                const orderNo = await orderNoJson.json();
                const orderStatus = "FAIL";
                fetch(`/api/order/${orderNo.toString()}/update-status`, {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(orderStatus),
                })
                    .then(res => {
                        if (res.ok) {
                            setTimeout(() => alert(`결제에 실패하였습니다. 에러 내용: ${rsp.error_msg}`), 100);
                            setTimeout(() => location.href = "/order/cart", 1000);
                            return res;
                        }
                    })
            }
        },
    );
}

async function checkout() {
    if (formChecked && confirm("주문 하시겠습니까?")) {
        const detailDtoList = [];
        cart.forEach(item =>{
            item.option.forEach(o => {
                detailDtoList.push({productId: item.productId, price: item.price, quantity: o.quantity, size: o.size, color: o.color});
            })
        })

        console.log(detailDtoList);

        let member;

        try {
            const response = await fetch("/api/order/member-info")
            member = await response.json();
        } catch (error) {
            alert("로그인이 만료되었습니다.");
            window.location.href = "/";
            return;
        }

        const payMethodVal = document.querySelector("select[name = 'payMethod']").value;

        const channelKey = channelKeyArray[parseInt(payMethodVal)];
        const payMethod = payMethodArray[parseInt(payMethodVal)];

        // 주문 정보
        let flag = document.querySelector("select[name = 'deliveryFlag']").value;
        let address = document.querySelector("input[name = 'address']").value;
        let postNo = document.querySelector("input[name = 'postNo']").value;
        let addressDetail = document.querySelector("input[name = 'addressDetail']").value;
        let addressee = document.querySelector("input[name = 'addressee']").value;
        let contact = document.querySelector("input[name = 'contact']").value;

        if (flag === "true") {
            address = member.address;
            postNo = member.postNo;
            addressDetail = member.addressDetail;
            addressee = member.name;
            contact = member.phone;
        }

        // 직접 입력 선택 여부 판단
        if (requirementSelect.value === "직접입력")
            requirement = requireTextarea.value;
        else
            requirement = requirementSelect.value;

        // 주문 정보 객체
        const orderRequestDto = {
            postNo, address, addressDetail, addressee, contact, member,
            requirement,
            orderStatus: "NEW",
            totalPrice: total,
            detailDtoList,
        }

        // 결제 정보
        const payInfo = {
            channelKey,
            pay_method: "card",
            merchant_uid: `payment-${crypto.randomUUID()}`, //상점에서 생성한 고유 주문번호
            name: `${cart[0].name} 외 ${cart.length - 1}개`,
            amount: total,
            buyer_email: "test@portone.io",
            buyer_name: member.name,
            buyer_tel: member.phone, //필수 파라미터 입니다.
            buyer_addr: member.address,
            buyer_postcode: member.postNo,
            m_redirect_url: "{모바일에서 결제 완료 후 리디렉션 될 URL}",
            // escrow: true, //에스크로 결제인 경우 설정
            // vbank_due: "YYYYMMDD",
            // bypass: {
            //     // PC 경우
            //     acceptmethod: "noeasypay", // 간편결제 버튼을 통합결제창에서 제외(PC)
            //     // acceptmethod: "cardpoint", // 카드포인트 사용시 설정(PC)
            //     // 모바일 경우
            //     P_RESERVED: "noeasypay=Y", // 간편결제 버튼을 통합결제창에서 제외(모바일)
            //     // P_RESERVED: "cp_yn=Y", // 카드포인트 사용시 설정(모바일)
            //     // P_RESERVED: "twotrs_bank=Y&iosapp=Y&app_scheme=your_app_scheme://", // iOS에서 계좌이체시 결제가 이뤄지던 앱으로 돌아가기
            // },
            // period: {
            //     from: "20240101", //YYYYMMDD
            //     to: "20301231", //YYYYMMDD
            // },
        };

        const paymentDto = {
            memberName: member.name,
            payMethod,
            amount: total,
            payStatus: "SUCCESS",
        }

        fetch("/api/order/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(orderRequestDto),
        })
            .then(async (response) => {
                if (response.ok) {
                    // 주문 성공시 결제 요청
                    requestPay(payInfo, paymentDto)

                    return response.json();

                    // 주문 저장 이후 로직

                } else {
                    alert("주문에 실패했습니다.");
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
            })
            .then((data) => console.log("Response:", data))
            .catch((error) => console.error("Error:", error));
    }
}

document.getElementById("checkout-btn").addEventListener("click", checkout);
document.getElementById("total-count").innerText = cart.length;
const requirementSelect = document.getElementById("requirement");
const requireTextarea = document.getElementById("message-textarea");
const contact = document.querySelector("input[name = 'contact']");
requirementSelect.addEventListener("change", () => {
    if (requirementSelect.value === "직접입력")
        requireTextarea.style.display = "block";
    else
        requireTextarea.style.display = "none";
});

deliveryFlag.addEventListener("change", () => {
    const postForm = document.getElementById("post-form");
    const view = document.getElementById("post-form-view");
    if (deliveryFlag.value === "true") {
        view.style.display = "none";
        postForm.classList.remove("needs-validation", "was-validated");
    } else {
        view.style.display = "block";
        postForm.classList.add("needs-validation", "was-validated");
    }
});

contact.addEventListener('input', () => {
    let value = contact.value.replace(/[^0-9]/g, ''); // 숫자만 남기기
    if (value.length > 3 && value.length <= 7)
        value = value.slice(0, 3) + '-' + value.slice(3);
    else if (value.length > 7)
        value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);

    contact.value = value;
});

renderProduct();