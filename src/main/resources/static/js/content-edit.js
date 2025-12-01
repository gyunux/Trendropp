document.addEventListener('DOMContentLoaded', () => {
        const form = document.getElementById('content-edit-form');
        const itemsContainer = document.getElementById('items-container');
        const addItemBtn = document.getElementById('add-item-btn');
        const contentIdInput = document.getElementById('contentId');

        // HTML(타임리프)에서 선언한 allBrands 가져오기
        // 만약 undefined라면 빈 배열로 처리해 에러 방지
        const brandList = (typeof allBrands !== 'undefined') ? allBrands : [];

        // ============================================================
        // 1. 아이템 추가 버튼 클릭 시 (파일 업로드 UI 포함)
        // ============================================================
        addItemBtn.addEventListener('click', () => {
            addItemRow();
        });

        function addItemRow() {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'item-row card mb-3 p-3 item-form-group';

            // 브랜드 옵션 HTML 생성
            // (brand.koreanName 사용 - HTML과 통일)
            const brandOptions = brandList.map(brand =>
                `<option value="${brand.id}">${brand.koreanName}</option>`
            ).join('');

            // 새 아이템 추가 시 HTML 구조
            // 기존 아이템과 달리 '이미지 파일(input type="file")'이 보임
            rowDiv.innerHTML = `
            <div class="item-row-content">
                
                <div class="item-image-wrapper">
                    <span class="item-image-label">새 이미지</span>
                    
                    <img class="item-image-thumb preview-img" style="display:none;">
                    
                    <div class="item-image-placeholder">
                        <span>이미지 선택</span>
                    </div>

                    <input type="file" class="item-image-file mt-2" accept="image/*" style="width: 100%; font-size: 0.8rem;">
                </div>

                <div class="item-inputs-wrapper">
                    <div class="d-flex justify-content-between mb-2">
                        <h5 class="m-0">새 아이템 (추가됨)</h5>
                        <button type="button" class="btn btn-danger btn-sm remove-item-btn">삭제</button>
                    </div>

                    <div class="form-row-2col">
                        <div class="form-group col-brand">
                            <label>브랜드</label>
                            <select class="item-brand form-control" required>
                                <option value="">브랜드 선택</option>
                                ${brandOptions}
                            </select>
                        </div>
                        <div class="form-group col-name">
                            <label>상품명</label>
                            <input type="text" class="item-name form-control" placeholder="상품명 입력" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>구매 링크</label>
                        <input type="url" class="item-url form-control" placeholder="https://...">
                    </div>
                </div>
            </div>
        `;

            itemsContainer.appendChild(rowDiv);
        }

        // ============================================================
        // 2. 이벤트 위임: 삭제 버튼 & 이미지 미리보기 기능
        // ============================================================
        itemsContainer.addEventListener('click', (e) => {
            // 삭제 버튼 클릭 시
            if (e.target.matches('.remove-item-btn')) {
                if (confirm('이 아이템을 삭제하시겠습니까?')) {
                    e.target.closest('.item-row').remove();
                }
            }
        });

        // 파일 선택 시 미리보기 (change 이벤트)
        itemsContainer.addEventListener('change', (e) => {
            if (e.target.matches('.item-image-file')) {
                const fileInput = e.target;
                const file = fileInput.files[0];

                // 현재 행(Row) 찾기
                const row = fileInput.closest('.item-image-wrapper');
                const previewImg = row.querySelector('.preview-img');
                const placeholder = row.querySelector('.item-image-placeholder');

                if (file) {
                    // 파일을 읽어서 미리보기 이미지 src에 할당
                    const reader = new FileReader();
                    reader.onload = function (event) {
                        previewImg.src = event.target.result;
                        previewImg.style.display = 'block';     // 이미지 보이기
                        if (placeholder) placeholder.style.display = 'none'; // 회색 박스 숨기기
                    }
                    reader.readAsDataURL(file);
                } else {
                    // 선택 취소 시 초기화
                    previewImg.src = '';
                    previewImg.style.display = 'none';
                    if (placeholder) placeholder.style.display = 'flex';
                }
            }
        });

        // ============================================================
        // 3. 폼 전송 (현재는 JSON 방식)
        // ============================================================
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const contentId = contentIdInput.value;
            const formData = new FormData();

            // 3-1. 기본 필드 추가
            formData.append('titleKo', document.getElementById('contentTitleKo').value);
            formData.append('titleEn', document.getElementById('contentTitleEn').value);
            formData.append('summaryKo', document.getElementById('summaryKo').value);
            formData.append('summaryEn', document.getElementById('summaryEn').value);
            formData.append('celebId', document.getElementById('celebId').value);
            formData.append('sourceType', document.getElementById('sourceType').value);
            formData.append('sourceUrl', document.getElementById('sourceUrl').value || '');
            formData.append('mainImageUrl', document.getElementById('mainImageUrl').value);

            // 3-2. 아이템 리스트 추가 (인덱싱 중요! items[0], items[1]...)
            const itemRows = itemsContainer.querySelectorAll('.item-row');

            itemRows.forEach((row, index) => {
                const brandSelect = row.querySelector('.item-brand');
                const nameInput = row.querySelector('.item-name');
                const urlInput = row.querySelector('.item-url');

                // 기존 이미지 URL (hidden input)
                const originalUrlInput = row.querySelector('.item-image-url');
                // 새 파일 Input
                const fileInput = row.querySelector('.item-image-file');

                if (brandSelect && nameInput) {
                    // Spring Binding을 위한 키 생성: items[0].brandId
                    formData.append(`items[${index}].brandId`, brandSelect.value);
                    formData.append(`items[${index}].itemName`, nameInput.value);
                    formData.append(`items[${index}].productUrl`, urlInput ? urlInput.value : '');

                    // 기존 이미지 URL 보내기
                    if (originalUrlInput && originalUrlInput.value) {
                        formData.append(`items[${index}].originalImageUrl`, originalUrlInput.value);
                    }

                    // [핵심] 새 파일이 선택되었으면 파일 보내기
                    if (fileInput && fileInput.files[0]) {
                        formData.append(`items[${index}].itemImageFile`, fileInput.files[0]);
                    }
                }
            });

            // 3-3. 전송
            try {
                const response = await fetch(`/api/admin/contents/${contentId}`, {
                    method: 'PUT',
                    // ⚠️ 주의: Content-Type 헤더를 설정하면 안 됨!
                    // 브라우저가 알아서 boundary를 포함한 multipart/form-data로 설정함.
                    body: formData
                });

                if (!response.ok) {
                    const errorData = await response.json(); // 혹은 text()
                    throw new Error(errorData.message || '수정에 실패했습니다.');
                }

                alert('콘텐츠가 성공적으로 수정되었습니다.');
                window.location.href = '/admin/contents';

            } catch (error) {
                console.error('Error:', error);
                alert(`에러 발생: ${error.message}`);
            }
        });
    }
)