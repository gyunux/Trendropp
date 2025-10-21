// 페이지의 모든 HTML 요소가 로드된 후에 이 코드를 실행합니다.
document.addEventListener('DOMContentLoaded', () => {

    // --- 사용할 HTML 요소들을 미리 찾아 변수에 저장 ---
    const contentForm = document.getElementById('content-create-form');
    const mainImageUrlInput = document.getElementById('mainImageUrl');
    const imagePreview = document.getElementById('image-preview');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/contents'; // 데이터를 보낼 서버의 API 주소

    // --- 1. 이미지 URL 입력 시 미리보기 보여주는 로직 ---
    mainImageUrlInput.addEventListener('input', (e) => {
        const imageUrl = e.target.value;
        if (imageUrl) {
            imagePreview.src = imageUrl;
            imagePreview.style.display = 'block'; // 이미지가 있으면 보이게
        } else {
            imagePreview.style.display = 'none'; // 주소가 비면 숨기기
        }
    });

    // --- 2. '아이템 추가' 버튼 클릭 시 폼을 동적으로 생성하는 로직 ---
    let itemCounter = 0;
    addItemBtn.addEventListener('click', () => {
        itemCounter++;
        // allBrands 변수는 HTML 파일 안의 <script> 태그에서 미리 준비해 둔 것입니다.
        const brandOptionsHtml = allBrands.map(brand =>
            `<option value="${brand.id}">${brand.englishName}</option>`
        ).join('');

        const itemFormHtml = `
            <div class="item-form-group" data-item-id="${itemCounter}">
                <h5>아이템 #${itemCounter}</h5>
                <button type="button" class="btn btn-danger btn-sm remove-item-btn">삭제</button>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="item-brand-${itemCounter}">브랜드</label>
                        <select id="item-brand-${itemCounter}" class="item-brand-id" required>${brandOptionsHtml}</select>
                    </div>
                    <div class="form-group">
                        <label for="item-name-${itemCounter}">아이템 이름</label>
                        <input type="text" id="item-name-${itemCounter}" class="item-name" required>
                    </div>
                    <div class="form-group">
                        <label for="item-image-url-${itemCounter}">아이템 이미지 URL</label>
                        <input type="url" id="item-image-url-${itemCounter}" class="item-image-url" required>
                    </div>
                    <div class="form-group">
                        <label for="item-product-url-${itemCounter}">구매 링크</label>
                        <input type="url" id="item-product-url-${itemCounter}" class="item-product-url" required>
                    </div>
                </div>
            </div>
        `;
        // 생성된 HTML 폼을 items-container 안에 추가합니다.
        itemsContainer.insertAdjacentHTML('beforeend', itemFormHtml);
    });

    // --- 3. '삭제' 버튼 클릭 시 해당 아이템 폼을 제거하는 로직 ---
    itemsContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-item-btn')) {
            e.target.closest('.item-form-group').remove();
        }
    });

    // --- 4. '착장 정보 저장' 버튼 클릭 시 전체 폼 데이터를 서버로 전송하는 로직 ---
    contentForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // 폼의 기본 제출 동작(새로고침)을 막습니다.

        // 동적으로 추가된 아이템 폼들의 데이터를 배열에 담습니다.
        const items = [];
        document.querySelectorAll('.item-form-group').forEach(itemGroup => {
            items.push({
                brandId: itemGroup.querySelector('.item-brand-id').value,
                itemName: itemGroup.querySelector('.item-name').value,
                itemImageUrl: itemGroup.querySelector('.item-image-url').value,
                productUrl: itemGroup.querySelector('.item-product-url').value
            });
        });

        // 서버로 보낼 최종 데이터(JSON)를 만듭니다.
        const formData = {
            title: document.getElementById('content-title').value,
            celebId: document.getElementById('celebId').value,
            sourceType: document.getElementById('sourceType').value,
            sourceUrl: '', // 현재 폼에는 없으므로 빈 값
            sourceDate: null, // 현재 폼에는 없으므로 null
            mainImageUrl: mainImageUrlInput.value,
            items: items
        };

        // 서버에 데이터를 전송합니다.
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (!response.ok) { // 서버에서 에러 응답이 온 경우
                throw new Error('착장 정보 저장에 실패했습니다. 입력값을 확인해주세요.');
            }

            alert('착장 정보가 성공적으로 저장되었습니다!');
            window.location.href = '/admin/contents'; // 저장 후 목록 페이지로 이동

        } catch (error) {
            console.error('Submit Error:', error); // 개발자를 위해 콘솔에 에러를 출력
            alert(error.message); // 사용자에게 에러 메시지 표시
        }
    });
});