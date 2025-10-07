document.addEventListener('DOMContentLoaded', () => {
    const outfitForm = document.getElementById('outfit-create-form');
    const mainImageUrlInput = document.getElementById('mainImageUrl');
    const imagePreview = document.getElementById('image-preview');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/outfits';

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

    // --- 2. 동적 아이템 폼 추가/삭제 로직 (기존과 동일) ---
    let itemCounter = 0;
    addItemBtn.addEventListener('click', () => {
        itemCounter++;
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

    // --- 3. 최종 폼 제출 로직 (기존과 거의 동일) ---
    outfitForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!mainImageUrlInput.value) {
            alert('대표 이미지 URL을 입력해주세요.');
            return;
        }

        const items = [];
        document.querySelectorAll('.item-form-group').forEach(itemGroup => {
            items.push({
                brandName: itemGroup.querySelector('.item-brand-id option:checked').text,
                itemName: itemGroup.querySelector('.item-name').value,
                itemImageUrl: itemGroup.querySelector('.item-image-url').value,
                productUrl: itemGroup.querySelector('.item-product-url').value
            });
        });

        const formData = {
            title: document.getElementById('outfit-title').value,
            celebId: document.getElementById('celebId').value,
            sourceType: document.getElementById('sourceType').value,
            // sourceUrl, sourceDate 등은 비워두거나 기본값을 설정
            sourceUrl: '',
            sourceDate: null,
            mainImageUrl: mainImageUrlInput.value,
            items: items
        };

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('착장 정보 저장에 실패했습니다.');
            alert('착장 정보가 성공적으로 저장되었습니다!');
            window.location.href = '/admin/outfits';
        } catch (error) {
            alert(error.message);
        }
    });
});