

// Array.from(statusButton).forEach(btn => {
//     const orderStatus = btn.value;
//     btn.addEventListener("click", () => {
//         fetch(`/api/v1/orders/${window.location.pathname.split("/").pop()}/update-status`, {
//             method: "PATCH",
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify(orderStatus),
//         })
//             .then((response) => {
//                 if (!response.ok) {
//                     alert("요청에 실패했습니다.");
//                     throw new Error(`HTTP error! status: ${response.status}`);
//                 } else {
//                     window.location.reload();
//                     return response.json();
//                 }
//             })
//             .then((data) => console.log("Response:", data))
//             .catch((error) => console.error("Error:", error))
//     })
// });

