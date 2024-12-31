function removeFavorite(productId) {
    fetch(`/api/favorites/${productId}`, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                alert('찜 목록에서 삭제되었습니다.');
                location.reload(); // 페이지 새로고침
            } else {
                alert('삭제에 실패했습니다.');
            }
        });
}