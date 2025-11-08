document.addEventListener('DOMContentLoaded', () => {

    // --- 1. HTML 요소 찾기 ---
    const contentForm = document.getElementById('content-create-form');
    const imageListContainer = document.querySelector('.image-list');
    const mainImageUrlInput = document.getElementById('mainImageUrl'); // 대표 이미지 S3 URL이 저장될 숨겨진 input
    const formPanel = document.getElementById('form-panel');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/contents';

    // --- 2. 대표 이미지 선택 로직 ---
    // (기사 이미지를 클릭하면 선택 상태로 만들고, 숨겨진 input에 S3 URL 저장)
    if (imageListContainer) {
        imageListContainer.addEventListener('click', (e) => {
            // 클릭한 요소가 '.selectable-image' 클래스를 가졌는지 확인
            if (e.target.classList.contains('selectable-image')) {

                // 기존에 'selected'된 이미지가 있다면 선택 해제
                const currentSelected = imageListContainer.querySelector('.selectable-image.selected');
                if (currentSelected) {
                    currentSelected.classList.remove('selected');
                }

                // 새로 클릭한 이미지에 'selected' 클래스 추가
                e.target.classList.add('selected');

                // [핵심] 숨겨진 input에 이 이미지의 S3 URL을 저장
                mainImageUrlInput.value = e.target.src;

                // 폼 패널 활성화
                formPanel.classList.remove('disabled-form');
            }
        });
    }

    // --- 3. '아이템 추가' 버튼 로직 ---
    // (아이템 폼을 동적으로 생성, 아이템 이미지는 "파일"로 받도록 수정됨)
    let itemCounter = 0;
    if (addItemBtn) {
        addItemBtn.addEventListener('click', () => {
            itemCounter++;
            // 'allBrands' 변수는 HTML의 <script th:inline="javascript">에서 가져옴
            const brandOptionsHtml = allBrands.map(brand =>
                // [수정] 관리자는 한국어 이름만 보도록 nameKo 사용
                `<option value="${brand.id}">${brand.nameKo}</option>`
            ).join('');

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
                            <label for="item-name-${itemCounter}">아이템 이름 (EN)</label>
                            <input type="text" id="item-name-${itemCounter}" class="item-name" placeholder="Item Name (EN)" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="item-image-file-${itemCounter}">아이템 이미지 (파일)</label>
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
    }

    // --- 4. '아이템 삭제' 및 '파일 이름 표시' 로직 (이벤트 위임) ---
    if (itemsContainer) {
        itemsContainer.addEventListener('click', (e) => {
            // '삭제' 버튼 클릭 시
            if (e.target.classList.contains('remove-item-btn')) {
                e.target.closest('.item-form-group').remove();
            }
        });

        // 아이템 폼의 '파일 선택' 시 파일 이름 표시
        itemsContainer.addEventListener('change', (e) => {
            if (e.target.classList.contains('item-image-file')) {
                const fileNameDisplay = e.target.closest('.file-upload-wrapper').querySelector('.item-file-name');
                fileNameDisplay.textContent = e.target.files.length > 0 ? e.target.files[0].name : '선택된 파일 없음';
            }
        });
    }


    // --- 5. [핵심] '콘텐츠 정보 저장' 폼 제출 로직 ---
    // (JSON이 아닌 FormData로 모든 데이터를 전송)
    if (contentForm) {
        contentForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // 폼 새로고침 방지

            const mainImageUrlValue = document.getElementById('mainImageUrl').value;
            if (!mainImageUrlValue) {
                alert('1. 이 기사의 대표 콘텐츠 이미지를 먼저 선택해주세요.');
                return;
            }

            // [핵심] FormData 객체 생성
            const formData = new FormData();

            // 1. 텍스트 데이터 추가 (KO/EN 포함)
            formData.append('titleKo', document.getElementById('contentTitleKo').value);
            formData.append('titleEn', document.getElementById('contentTitleEn').value);
            formData.append('summaryKo', document.getElementById('summaryKo').value);
            formData.append('summaryEn', document.getElementById('summaryEn').value);
            formData.append('celebId', document.getElementById('celebId').value);
            formData.append('sourceType', document.getElementById('sourceType').value);
            formData.append('sourceArticleId', document.getElementById('sourceArticleId').value);

            // 2. 대표 이미지 (선택한 S3 "URL" String) 추가
            formData.append('mainImageUrl', mainImageUrlInput.value);
            // (백엔드 SaveContentRequest DTO는 이 필드를 String으로 받아야 함)

            // 3. 아이템 정보 (텍스트 + "파일") 추가
            document.querySelectorAll('.item-form-group').forEach((itemGroup, index) => {
                formData.append(`items[${index}].brandId`, itemGroup.querySelector('.item-brand-id').value);
                formData.append(`items[${index}].itemName`, itemGroup.querySelector('.item-name').value);
                formData.append(`items[${index}].productUrl`, itemGroup.querySelector('.item-product-url').value);

                // [핵심] 아이템 이미지 "파일 객체" 추가
                const itemImageInput = itemGroup.querySelector('.item-image-file');
                if (itemImageInput.files.length > 0) {
                    formData.append(`items[${index}].itemImageFile`, itemImageInput.files[0]);
                }
            });

            // 4. Fetch API로 FormData 전송
            try {
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    body: formData // headers에 'Content-Type'을 지정하지 않음! (브라우저가 자동으로 multipart/form-data로 설정)
                });

                if (!response.ok) {
                    throw new Error('콘텐츠 정보 저장에 실패했습니다.');
                }

                alert('콘텐츠 정보가 성공적으로 저장되었습니다!');
                window.location.href = '/admin/contents'; // 저장 후 목록 페이지로 이동

            } catch (error) {
                console.error('Submit Error:', error);
                alert(error.message);
            }
        });
    }
});