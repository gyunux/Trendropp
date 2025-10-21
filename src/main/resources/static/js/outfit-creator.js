document.addEventListener('DOMContentLoaded', () => {
    const imageList = document.querySelector('.image-list');
    const formPanel = document.getElementById('form-panel');
    const outfitForm = document.getElementById('outfit-create-form');
    const mainImageUrlInput = document.getElementById('mainImageUrl');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/outfits';

    // --- 1. 이미지 선택 로직 ---
    imageList.addEventListener('click', (e) => {
        if (e.target.classList.contains('selectable-image')) {
            // 다른 이미지의 'selected' 스타일 모두 제거
            imageList.querySelectorAll('.selectable-image').forEach(img => img.classList.remove('selected'));

            // 클릭된 이미지에만 'selected' 스타일 추가
            const selectedImage = e.target;
            selectedImage.classList.add('selected');

            // 숨겨진 input에 선택된 이미지의 URL 저장
            mainImageUrlInput.value = selectedImage.src;

            // 폼 활성화
            formPanel.classList.remove('disabled-form');
        }
    });

    // --- 2. 동적 아이템 폼 추가/삭제 로직 ---
    let itemCounter = 0;
    addItemBtn.addEventListener('click', () => {
        itemCounter++;
        // HTML로부터 전달받은 allBrands 변수를 사용해 브랜드 드롭다운 생성
        const brandOptionsHtml = allBrands.map(brand =>
            `<option value="${brand.id}">${brand.englishName}</option>`
        ).join('');

        const itemFormHtml = `
            <div class="item-form-group">
                <h5>아이템 #${itemCounter}</h5>
                <div class="form-group">
                    <label>브랜드</label>
                    <select class="item-brand-id" required>${brandOptionsHtml}</select>
                </div>
                <div class="form-group">
                    <label>아이템 이름</label>
                    <input type="text" class="item-name" required>
                </div>
                <div class="form-group">
                    <label>아이템 이미지 URL</label>
                    <input type="url" class="item-image-url" required>
                </div>
                <div class="form-group">
                    <label>구매 링크</label>
                    <input type="url" class="item-product-url" required>
                </div>
                <button type="button" class="btn btn-danger btn-sm remove-item-btn">삭제</button>
            </div>
        `;
        itemsContainer.insertAdjacentHTML('beforeend', itemFormHtml);
    });

    itemsContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-item-btn')) {
            e.target.closest('.item-form-group').remove();
        }
    });

    // --- 3. 최종 폼 제출 로직 ---
    outfitForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!mainImageUrlInput.value) {
            alert('대표 이미지를 먼저 선택해주세요.');
            return;
        }

        const items = [];
        document.querySelectorAll('.item-form-group').forEach(itemGroup => {
            items.push({
                brandId: itemGroup.querySelector('.item-brand-id').value,
                itemName: itemGroup.querySelector('.item-name').value,
                itemImageUrl: itemGroup.querySelector('.item-image-url').value,
                productUrl: itemGroup.querySelector('.item-product-url').value
            });
        });

        const formData = {
            title: document.getElementById('outfit-title').value,
            celebId: document.getElementById('celebId').value,
            sourceArticleId: document.getElementById('sourceArticleId').value,
            sourceType: document.getElementById('sourceType').value,
            sourceUrl: document.querySelector('.article-title a')?.href || '', // 원본 기사 링크 가져오기 (필요시 수정)
            sourceDate: null, // 날짜 정보가 있다면 추가
            mainImageUrl: mainImageUrlInput.value,
            items: items,
            summary: document.getElementById('summary').value
        };

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('착장 정보 저장에 실패했습니다.');
            alert('착장 정보가 성공적으로 저장되었습니다!');
            window.location.href = '/admin/outfits'; // 저장 후 착장 관리 페이지로 이동
        } catch (error) {
            alert(error.message);
        }
    });
});