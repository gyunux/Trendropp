// 페이지의 모든 HTML 요소가 로드된 후에 이 코드를 실행합니다.
document.addEventListener('DOMContentLoaded', () => {

    // --- 사용할 HTML 요소들을 미리 찾아 변수에 저장 ---
    const contentForm = document.getElementById('content-create-form');
    const mainImageFileInput = document.getElementById('mainImageFile');
    const fileNameDisplay = document.getElementById('file-name-display');
    const imagePreview = document.getElementById('image-preview');
    const itemsContainer = document.getElementById('items-container');
    const addItemBtn = document.getElementById('add-item-btn');
    const API_BASE_URL = '/api/admin/contents'; // 데이터를 보낼 서버의 API 주소

    // --- 1. 이미지 URL 입력 시 미리보기 보여주는 로직 ---
    if (mainImageFileInput) {
        mainImageFileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (!file) { // 파일 선택을 취소한 경우
                fileNameDisplay.textContent = '선택된 파일 없음';
                imagePreview.style.display = 'none';
                imagePreview.src = '';
                return;
            }

            // [추가] 1. 파일 이름 표시
            fileNameDisplay.textContent = file.name;

            // [추가] 2. 이미지 미리보기 기능
            const reader = new FileReader();
            reader.onload = (event) => {
                imagePreview.src = event.target.result;
                imagePreview.style.display = 'block';
            };
            reader.readAsDataURL(file); // 파일을 읽어서 Base64 URL로 변환
        });
    }

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
        // 생성된 HTML 폼을 items-container 안에 추가합니다.
        itemsContainer.insertAdjacentHTML('beforeend', itemFormHtml);
    });

    // --- 3. '삭제' 버튼 클릭 시 해당 아이템 폼을 제거하는 로직 ---
    itemsContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-item-btn')) {
            e.target.closest('.item-form-group').remove();
        }
    });

    itemsContainer.addEventListener('change', (e) => {
        // 클릭된 요소가 아이템 이미지 파일 input인지 확인
        if (e.target.classList.contains('item-image-file')) {
            const fileNameDisplay = e.target.closest('.file-upload-wrapper').querySelector('.item-file-name');
            if (e.target.files.length > 0) {
                fileNameDisplay.textContent = e.target.files[0].name;
            } else {
                fileNameDisplay.textContent = '선택된 파일 없음';
            }
        }
    });

    // --- 4. '착장 정보 저장' 버튼 클릭 시 전체 폼 데이터를 서버로 전송하는 로직 ---
    contentForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData();
        formData.append('title', document.getElementById('content-title').value);
        formData.append('celebId', document.getElementById('celebId').value);
        formData.append('sourceType', document.getElementById('sourceType').value);

        // 대표 이미지 파일 추가
        if (mainImageFileInput.files.length > 0) {
            formData.append('mainImageFile', mainImageFileInput.files[0]);
        }

        // 아이템 정보들을 배열 형태로 추가 (items[0].brandId, items[1].brandId ...)
        document.querySelectorAll('.item-form-group').forEach((itemGroup, index) => {
            formData.append(`items[${index}].brandId`, itemGroup.querySelector('.item-brand-id').value);
            formData.append(`items[${index}].itemName`, itemGroup.querySelector('.item-name').value);
            const itemImageInput = itemGroup.querySelector('.item-image-file');
            if (itemImageInput.files.length > 0) {
                formData.append(`items[${index}].itemImageFile`, itemImageInput.files[0]);
            }            formData.append(`items[${index}].productUrl`, itemGroup.querySelector('.item-product-url').value);
        });

        try {
            const response = await fetch('/api/admin/contents', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('콘텐츠 저장에 실패했습니다. 입력값을 확인해주세요.');
            }

            alert('콘텐츠가 성공적으로 저장되었습니다!');
            window.location.href = '/admin/contents';

        } catch (error) {
            console.error('Submit Error:', error);
            alert(error.message);
        }
    });
});