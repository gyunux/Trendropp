document.addEventListener('DOMContentLoaded', () => {
    fetchFashionItems();
});

// 백엔드 API로부터 패션 아이템 데이터를 가져오는 함수
async function fetchFashionItems() {
    try {
        const response = await fetch('/api/fashion-items'); // 우리 백엔드 API 호출!
        if (!response.ok) {
            throw new Error('데이터를 불러오는 데 실패했습니다.');
        }
        const items = await response.json();
        displayFashionItems(items); // 데이터를 화면에 표시하는 함수 호출
    } catch (error) {
        console.error(error);
        const container = document.getElementById('fashion-container');
        container.innerHTML = '<p>정보를 불러올 수 없습니다.</p>';
    }
}

// 가져온 데이터를 기반으로 HTML 카드를 만들어 화면에 추가하는 함수
function displayFashionItems(items) {
    const container = document.getElementById('fashion-container');
    container.innerHTML = ''; // 기존 내용을 비웁니다.

    items.forEach(item => {
        // 각 아이템에 대한 HTML 카드 생성
        const card = document.createElement('div');
        card.className = 'fashion-card';

        card.innerHTML = `
            <a href="${item.originalArticleUrl}" target="_blank">
                <img src="${item.imageUrl}" alt="${item.celebrityName}의 패션">
                <div class="card-info">
                    <p>${item.celebrityName}</p>
                </div>
            </a>
        `;

        container.appendChild(card); // 생성된 카드를 컨테이너에 추가
    });
}