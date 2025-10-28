document.addEventListener('DOMContentLoaded', () => {
    const imageList = document.querySelector('.image-list');
    const formPanel = document.getElementById('form-panel');
    const contentForm = document.getElementById('content-create-form');
    const mainImageUrlInput = document.getElementById('mainImageUrl');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/contents';

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
        const brandOptionsHtml = allBrands.map(brand =>
            `<option value="${brand.id}">${brand.englishName}</option>`
        ).join('');

        // [수정] 아이템 이미지 URL 입력창을 파일 선택 UI로 변경
        const itemFormHtml = `
        <div class="item-form-group" data-item-id="${itemCounter}">
            <h5>아이템 #${itemCounter}</h5>
            <button type="button" class="btn btn-danger btn-sm remove-item-btn">삭제</button>
            <div class="form-grid">
                <div class="form-group">
                    <label for="item-brand-${itemCounter}">브랜드</label>
                    <select id="item-brand-${itemCounter}" class="item-brand-id" required>
                        <option value="">-- 브랜드 선택 --</option>
                        ${brandOptionsHtml}
                    </select>
                </div>
                <div class="form-group">
                    <label for="item-name-${itemCounter}">아이템 이름</label>
                    <input type="text" id="item-name-${itemCounter}" class="item-name" required>
                </div>

                <div class="form-group">
                    <label for="item-image-file-${itemCounter}">아이템 이미지</label>
                    <div class="file-upload-wrapper">
                        <input type="file" id="item-image-file-${itemCounter}" class="item-image-file" accept="image/*" required>
                        <label for="item-image-file-${itemCounter}" class="btn btn-secondary btn-sm">파일 찾기</label>
                        <span class="item-file-name">선택된 파일 없음</span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="item-product-url-${itemCounter}">구매 링크</label>
                    <input type="url" id="item-product-url-${itemCounter}" class="item-product-url" required>
                </div>
            </div>
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
    contentForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        // [핵심] 사용자가 선택한 이미지의 S3 URL 가져오기
        const selectedImageElement = document.querySelector('.selectable-image.selected'); // 이미지 선택 로직에 따라 달라질 수 있음
        if (!selectedImageElement) {
            alert('대표 이미지를 먼저 선택해주세요.');
            return;
        }
        const mainImageUrlValue = selectedImageElement.src; // 선택된 이미지의 src (S3 URL)

        const items = [];
        document.querySelectorAll('.item-form-group').forEach(itemGroup => {
            items.push({
                brandId: itemGroup.querySelector('.item-brand-id').value,
                itemName: itemGroup.querySelector('.item-name').value,
                // [수정] 아이템 이미지도 URL 방식으로 가정 (필요시 파일 업로드 로직 추가)
                itemImageUrl: itemGroup.querySelector('.item-image-url').value,
                productUrl: itemGroup.querySelector('.item-product-url').value
            });
        });

        // [수정] JSON 객체 생성
        const formData = {
            title: document.getElementById('content-title').value,
            celebId: document.getElementById('celebId').value,
            sourceArticleId: document.getElementById('sourceArticleId').value, // hidden input 값
            sourceType: document.getElementById('sourceType').value,
            summary: document.getElementById('summary').value, // textarea 값

            mainImageUrl: mainImageUrlValue, // [수정] 파일 대신 S3 URL String

            items: items
            // sourceUrl, sourceDate 등 필요시 추가
        };

        try {
            const response = await fetch(API_BASE_URL, { // API_BASE_URL = '/api/admin/contents'
                method: 'POST',
                // [수정] 다시 JSON 방식으로 변경
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('콘텐츠 정보 저장에 실패했습니다.');
            alert('콘텐츠 정보가 성공적으로 저장되었습니다!');
            window.location.href = '/admin/contents';
        } catch (error) {
            alert(error.message);
        }
    });
});