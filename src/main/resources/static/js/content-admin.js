document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.getElementById('content-table-body');
    const API_BASE_URL = '/api/admin/contents';

    // =========================================================
    // 1. 검색 기능 (DOM 기반 필터링)
    // =========================================================
    const searchInput = document.getElementById('content-search-input');

    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        tableBody.querySelectorAll('tr').forEach(row => {
            // 데이터가 없을 때 표시되는 행(.no-data)은 검색 대상에서 제외
            if (row.querySelector('.no-data')) return;

            const celebName = row.querySelector('.content-celeb-name')?.textContent.toLowerCase() || '';
            // 필요하다면 제목 등으로 검색 조건 추가 가능
            // const title = row.dataset.titleKo?.toLowerCase() || '';

            // 검색어가 포함되어 있으면 보여주고, 아니면 숨김
            row.style.display = celebName.includes(searchTerm) ? '' : 'none';
        });
    });

    // =========================================================
    // 2. 삭제 기능 (이벤트 위임)
    // =========================================================
    tableBody.addEventListener('click', (e) => {
        // 수정 버튼은 이제 <a> 태그라서 JS 이벤트가 필요 없음
        // 삭제 버튼 클릭만 처리
        if (e.target.matches('.delete-btn')) {
            const row = e.target.closest('tr');
            if (!row) return;

            const contentId = row.dataset.id;

            if (confirm(`착장(ID: ${contentId})을 정말로 삭제하시겠습니까?`)) {
                deleteContent(contentId);
            }
        }
    });

    async function deleteContent(contentId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${contentId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('삭제 실패');
            }

            alert('성공적으로 삭제되었습니다.');
            window.location.reload(); // 새로고침하여 목록 갱신
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    }
});