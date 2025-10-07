document.addEventListener('DOMContentLoaded', () => {

    /**
     * 사이드바에서 현재 페이지의 URL과 일치하는 링크에 'active' 클래스를 추가하는 함수
     */
    function setActiveSidebarLink() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.sidebar ul li a');

        navLinks.forEach(link => {
            // 기존 active 클래스를 먼저 제거 (다른 페이지에서 왔을 경우 대비)
            link.classList.remove('active');
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            }
        });
    }

    /**
     * 공개용 착장(Outfit) 데이터를 API로 불러와 화면에 카드 형태로 표시하는 함수
     */
    async function loadOutfits() {
        const container = document.getElementById('fashion-container');
        if (!container) return; // fashion-container가 없는 페이지면 실행 안 함

        try {
            // API 경로를 일관성 있게 '/api/public/...'으로 수정
            const response = await fetch('/api/public/outfits');
            if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');

            const outfits = await response.json();

            container.innerHTML = ''; // 기존 콘텐츠 비우기
            outfits.forEach(outfit => {
                const card = document.createElement('a');
                card.className = 'fashion-card';
                card.href = `/outfits/${outfit.id}`; // 상세 페이지 링크

                // ▼▼▼ 카드에 'title'을 포함하도록 innerHTML 수정 ▼▼▼
                card.innerHTML = `
                    <div class="card-image">
                        <img src="${outfit.originImageUrl}" alt="${outfit.title}">
                    </div>
                    <div class="card-content">
                        <h3 class="outfit-title">${outfit.title}</h3>
                        <p class="celeb-name">${outfit.celeb.name}</p>
                        <p class="item-count">아이템 ${outfit.itemCount}개</p>
                    </div>
                `;
                container.appendChild(card);
            });
        } catch (error) {
            console.error('Error fetching outfits:', error);
            container.innerHTML = '<p>콘텐츠를 불러올 수 없습니다.</p>';
        }
    }

    // --- 페이지 로드 시 함수 실행 ---
    setActiveSidebarLink();
    loadOutfits();
});