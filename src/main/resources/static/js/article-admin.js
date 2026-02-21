// DOM이 완전히 로드되면 내부의 모든 코드를 실행합니다.
document.addEventListener('DOMContentLoaded', () => {

    // --- 사이드바 활성화 로직 ---
    // 현재 페이지 URL과 일치하는 사이드바 링크에 'active' 클래스를 추가합니다.
    function setActiveSidebarLink() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('#admin-sidebar-nav li a');
        navLinks.forEach(link => {
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            }
        });
    }

    // --- 기사 삭제 로직 ---
    const articleTableBody = document.getElementById('article-table-body');
    const API_BASE_URL = '/api/admin/articles'; // API 경로

    // 기사 삭제 API를 호출하는 함수
    async function deleteArticle(articleId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${articleId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('기사 삭제에 실패했습니다.');
            }

            alert('기사가 성공적으로 삭제되었습니다.');
            window.location.reload(); // 성공 시 페이지 새로고침
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }

    // 테이블에 클릭 이벤트 리스너 추가 (이벤트 위임)
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

    // --- 초기화 함수 호출 ---
    // 페이지가 로드되면 사이드바 활성화 함수를 즉시 실행합니다.
    setActiveSidebarLink();

});
async function triggerCrawling() {
    const btn = document.getElementById('crawlBtn');
    const originalText = btn.innerText;

    if (!confirm("최신 기사를 크롤링하시겠습니까?")) {
        return;
    }

    btn.innerText = "⏳ 크롤링 진행 중...";
    btn.disabled = true;

    try {
        const response = await fetch('/api/admin/crawl', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert("✅ 크롤링이 백그라운드에서 시작되었습니다!\n이제 다른 페이지로 자유롭게 이동하셔도 됩니다.");
            window.location.reload(); // 목록 갱신
        } else if (response.status === 403) {
            alert("❌ 권한 없음: 관리자 권한이 필요합니다.");
        } else {
            const errorText = await response.text();
            alert("❌ 서버 오류: " + errorText);
        }

    } catch (error) {
        console.error("Error:", error);
        alert("❌ 네트워크 오류: 서버와 연결할 수 없습니다.");
    } finally {
        btn.innerText = originalText;
        btn.disabled = false;
    }
}