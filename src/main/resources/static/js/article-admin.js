document.addEventListener('DOMContentLoaded', () => {
    const articleTableBody = document.getElementById('article-table-body');

    if (articleTableBody) {
        articleTableBody.addEventListener('click', (event) => {
            // 클릭된 요소가 '삭제' 버튼인지 확인
            if (event.target.classList.contains('delete-article-btn')) {
                const row = event.target.closest('tr');
                const articleId = row.dataset.id;

                if (confirm(`정말로 이 기사(ID: ${articleId})를 삭제하시겠습니까?`)) {
                    deleteArticle(articleId);
                }
            }
        });
    }
});

/**
 * 기사 삭제 API를 호출하는 함수
 * @param {string} articleId - 삭제할 기사의 ID
 */
async function deleteArticle(articleId) {
    try {
        const response = await fetch(`/api/v1/articles/${articleId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('기사 삭제에 실패했습니다.');
        }

        alert('기사가 성공적으로 삭제되었습니다.');
        // 성공 시 페이지를 새로고침하여 목록을 갱신
        window.location.reload();
    } catch (error) {
        console.error('Error:', error);
        alert(error.message);
    }
}

function setActiveSidebarLink() {
    // 1. 현재 페이지의 URL 경로를 가져옵니다. (예: "/admin/articles")
    const currentPath = window.location.pathname;

    // 2. 사이드바 내비게이션 링크들을 모두 찾습니다.
    const navLinks = document.querySelectorAll('#admin-sidebar-nav li a');

    // 3. 각 링크를 순회하며 확인합니다.
    navLinks.forEach(link => {
        // 4. 링크의 href 속성값과 현재 URL 경로가 일치하는지 확인합니다.
        if (link.getAttribute('href') === currentPath) {
            // 5. 일치한다면, 해당 링크에 'active' 클래스를 추가합니다.
            link.classList.add('active');
        }
    });
}

// DOM이 완전히 로드된 후 함수를 실행합니다.
document.addEventListener('DOMContentLoaded', setActiveSidebarLink);