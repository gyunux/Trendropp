document.addEventListener('DOMContentLoaded', () => {

    // --- 1. HTML 요소 찾기 ---
    const contentForm = document.getElementById('content-create-form');
    const imageListContainer = document.querySelector('.image-list');
    const mainImageUrlInput = document.getElementById('mainImageUrl');
    const formPanel = document.getElementById('form-panel');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/contents';

    // --- 2. 대표 이미지 선택 로직 ---
    if (imageListContainer) {
        imageListContainer.addEventListener('click', (e) => {
            if (e.target.classList.contains('selectable-image')) {
                const currentSelected = imageListContainer.querySelector('.selectable-image.selected');
                if (currentSelected) currentSelected.classList.remove('selected');

                e.target.classList.add('selected');
                mainImageUrlInput.value = e.target.src;
                formPanel.classList.remove('disabled-form');
            }
        });
    }

    // --- 3. 아이템 추가 버튼 ---
    let itemCounter = 0;
    if (addItemBtn) {
        addItemBtn.addEventListener('click', () => {
            itemCounter++;

            const itemFormHtml = `
                <div class="item-form-group" data-item-id="${itemCounter}">
                    <h5>아이템 #${itemCounter}</h5>
                    <button type="button" class="btn btn-danger btn-sm remove-item-btn">삭제</button>
                    <div class="form-grid">

                        <!-- 브랜드 검색 -->
                        <div class="form-group">
                            <label for="item-brand-search-${itemCounter}">브랜드 검색</label>
                            <input type="text" id="item-brand-search-${itemCounter}" 
                                class="item-brand-search" placeholder="브랜드명 검색..." autocomplete="off">
                            <div class="brand-search-results" id="brand-search-results-${itemCounter}"></div>
                            <input type="hidden" class="item-brand-id" id="item-brand-id-${itemCounter}">
                        </div>

                        <div class="form-group">
                            <label for="item-name-${itemCounter}">아이템 이름 (EN)</label>
                            <input type="text" id="item-name-${itemCounter}" class="item-name" required>
                        </div>

                        <div class="form-group">
                            <label for="item-image-file-${itemCounter}">아이템 이미지 (파일)</label>
                            <div class="file-upload-wrapper">
                                <input type="file" id="item-image-file-${itemCounter}" 
                                       class="item-image-file" accept="image/*" required>
                                <label for="item-image-file-${itemCounter}" 
                                       class="btn btn-secondary btn-sm">파일 찾기</label>
                                <span class="item-file-name">선택된 파일 없음</span>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="item-product-url-${itemCounter}">구매 링크</label>
                            <input type="url" id="item-product-url-${itemCounter}" 
                                   class="item-product-url" placeholder="https://..." required>
                        </div>
                    </div>
                </div>
            `;
            itemsContainer.insertAdjacentHTML('beforeend', itemFormHtml);
        });
    }

    // --- 4. 아이템 삭제 / 파일 이름 표시 / 브랜드 검색 로직 ---
    if (itemsContainer) {
        itemsContainer.addEventListener('click', (e) => {
            if (e.target.classList.contains('remove-item-btn')) {
                e.target.closest('.item-form-group').remove();
            }
        });

        itemsContainer.addEventListener('change', (e) => {
            if (e.target.classList.contains('item-image-file')) {
                const fileNameDisplay = e.target.closest('.file-upload-wrapper')
                    .querySelector('.item-file-name');
                fileNameDisplay.textContent = e.target.files.length > 0
                    ? e.target.files[0].name
                    : '선택된 파일 없음';
            }
        });

        // 브랜드 검색 자동완성
        itemsContainer.addEventListener('input', (e) => {
            if (e.target.classList.contains('item-brand-search')) {
                const searchTerm = e.target.value.toLowerCase();
                const resultsBox = e.target.nextElementSibling;
                const hiddenInput = e.target.parentElement.querySelector('.item-brand-id');

                resultsBox.innerHTML = '';

                if (!searchTerm) {
                    resultsBox.style.display = 'none';
                    return;
                }

                // allBrands는 Thymeleaf로 주입됨
                const matches = allBrands.filter(b =>
                    b.koreanName.toLowerCase().includes(searchTerm) ||
                    b.englishName?.toLowerCase().includes(searchTerm)
                );

                if (matches.length === 0) {
                    resultsBox.innerHTML = `<div class="no-results">검색 결과 없음</div>`;
                    resultsBox.style.display = 'block';
                    hiddenInput.value = '';
                    return;
                }

                const inputRect = e.target.getBoundingClientRect();
                const spaceBelow = window.innerHeight - inputRect.bottom;
                if (spaceBelow < 180) {
                    resultsBox.style.top = 'auto';
                    resultsBox.style.bottom = '100%';   // 위로 띄우기
                } else {
                    resultsBox.style.bottom = 'auto';
                    resultsBox.style.top = '100%';      // 아래로 띄우기 (기본)
                }

                resultsBox.style.display = 'block';
                matches.slice(0, 10).forEach(brand => {
                    const div = document.createElement('div');
                    div.classList.add('brand-option');
                    div.textContent = `${brand.koreanName} (${brand.englishName || ''})`;
                    div.dataset.id = brand.id;
                    div.addEventListener('click', () => {
                        e.target.value = brand.koreanName;
                        hiddenInput.value = brand.id;
                        resultsBox.innerHTML = '';
                        resultsBox.style.display = 'none';
                    });
                    resultsBox.appendChild(div);
                });
            }
        });

        // 입력창 클릭 외부를 클릭하면 자동완성창 닫기
        document.addEventListener('click', (e) => {
            if (!e.target.classList.contains('item-brand-search') &&
                !e.target.classList.contains('brand-option')) {
                document.querySelectorAll('.brand-search-results').forEach(box => {
                    box.style.display = 'none';
                });
            }
        });
    }

    // --- 5. 콘텐츠 저장 로직 ---
    if (contentForm) {
        contentForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const mainImageUrlValue = mainImageUrlInput.value;
            if (!mainImageUrlValue) {
                alert('대표 이미지를 선택해주세요.');
                return;
            }

            const formData = new FormData();

            formData.append('titleKo', document.getElementById('contentTitleKo').value);
            formData.append('titleEn', document.getElementById('contentTitleEn').value);
            formData.append('summaryKo', document.getElementById('summaryKo').value);
            formData.append('summaryEn', document.getElementById('summaryEn').value);
            formData.append('celebId', document.getElementById('celebId').value);
            formData.append('sourceType', document.getElementById('sourceType').value);
            formData.append('sourceArticleId', document.getElementById('sourceArticleId').value);
            formData.append('sourceUrl', document.getElementById('sourceUrl').value);
            formData.append('mainImageUrl', mainImageUrlInput.value);

            document.querySelectorAll('.item-form-group').forEach((itemGroup, index) => {
                formData.append(`items[${index}].brandId`, itemGroup.querySelector('.item-brand-id').value);
                formData.append(`items[${index}].itemName`, itemGroup.querySelector('.item-name').value);
                formData.append(`items[${index}].productUrl`, itemGroup.querySelector('.item-product-url').value);

                const itemImageInput = itemGroup.querySelector('.item-image-file');
                if (itemImageInput.files.length > 0) {
                    formData.append(`items[${index}].itemImageFile`, itemImageInput.files[0]);
                }
            });

            try {
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    throw new Error('콘텐츠 정보 저장에 실패했습니다.');
                }

                alert('콘텐츠가 성공적으로 저장되었습니다!');
                window.location.href = '/admin/contents';
            } catch (error) {
                console.error('Submit Error:', error);
                alert(error.message);
            }
        });
    }
});
