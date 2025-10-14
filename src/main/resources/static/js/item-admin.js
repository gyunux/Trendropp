document.addEventListener('DOMContentLoaded', () => {
    // API 베이스 URL 정의
    const API_BASE_URL = '/api/admin/items';

    // 1. 필요한 DOM 요소 정의
    const tableBody = document.querySelector('.data-table tbody');

    const addItemBtn = document.getElementById('add-item-btn');
    const addItemModal = document.getElementById('add-item-modal');
    const addItemForm = document.getElementById('add-item-form');
    const addBrandSelect = document.getElementById('add-brand-id'); // 등록 모달 브랜드 select

    const editModal = document.getElementById('edit-item-modal');
    const editForm = document.getElementById('edit-item-form');
    const closeEditButtons = editModal.querySelectorAll('.item-close-btn, .item-cancel-btn');

    let allBrands = []; // 모든 브랜드 데이터를 저장할 배열


    // --- 2. 초기 데이터 로드 (브랜드) ---
    async function loadInitialData() {
        try {
            const brandsRes = await fetch('/api/admin/brands'); // 브랜드 목록 API 호출
            if (!brandsRes.ok) throw new Error('브랜드 데이터 로딩 실패');

            allBrands = await brandsRes.json();

            // 등록 모달의 브랜드 드롭다운 채우기
            populateBrandSelect(addBrandSelect, allBrands);

        } catch (error) {
            console.error('초기 데이터 로딩 오류:', error);
            alert('브랜드 데이터를 가져오는 중 오류가 발생했습니다.');
        }
    }

    // 드롭다운 요소에 옵션 채우는 헬퍼 함수
    function populateBrandSelect(selectElement, brands, selectedId = null) {
        // 기존 옵션 (선택 안 함)을 제외한 나머지 삭제
        selectElement.innerHTML = '<option value="">-- 브랜드를 선택하세요 --</option>';

        brands.forEach(brand => {
            const option = document.createElement('option');
            option.value = brand.id;
            option.textContent = brand.englishName;
            if (selectedId && brand.id.toString() === selectedId.toString()) {
                option.selected = true; // 수정 시 선택된 값 설정
            }
            selectElement.appendChild(option);
        });
    }

    // --- 3. 모달 제어 함수 ---

    // 수정 모달 닫기
    const closeEditModal = () => {
        editModal.classList.remove('active');
        editForm.reset();
    };

    // 등록 모달 닫기
    const closeAddModal = () => {
        addItemModal.classList.remove('active');
        addItemForm.reset();
    };

    // --- 4. 이벤트 리스너 설정 ---

    // 4.1. 수정 모달 닫기 이벤트
    closeEditButtons.forEach(btn => {
        btn.addEventListener('click', closeEditModal);
    });
    editModal.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal-overlay')) {
            closeEditModal();
        }
    });

    // 4.2. 등록 모달 열기/닫기 이벤트
    addItemBtn.addEventListener('click', () => {
        closeEditModal(); // 혹시 열려있는 다른 모달 닫기
        addItemForm.reset();
        addItemModal.classList.add('active');
    });
    addItemModal.querySelectorAll('#close-add-modal-btn, #cancel-add-btn').forEach(btn => {
        btn.addEventListener('click', closeAddModal);
    });
    addItemModal.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal-overlay')) {
            closeAddModal();
        }
    });


    // --- 5. 테이블 액션 버튼 처리 (수정, 삭제) ---
    tableBody.addEventListener('click', (e) => {
        const row = e.target.closest('tr');
        if (!row) return;

        const itemId = row.getAttribute('data-id');

        // 5.1. 수정 버튼 (Edit) 처리
        if (e.target.classList.contains('edit-btn')) {
            // Thymeleaf에서 th:attr로 심어둔 data 속성 가져오기
            const brandId = row.getAttribute('data-brand-id');
            const itemName = row.children[1].textContent;
            const brandName = row.children[2].textContent;
            const imageUrl = row.querySelector('.thumbnail-img')?.getAttribute('src');
            const productUrlLink = row.querySelector('.product-url a');
            const productUrl = productUrlLink ? productUrlLink.getAttribute('href') : '';

            // 1. 모달 필드에 데이터 설정
            document.getElementById('edit-item-id').value = itemId;
            document.getElementById('edit-brand-id').value = brandId; // 숨김 필드에 ID 설정
            document.getElementById('edit-item-brand-name').value = brandName; // Readonly 필드에 이름 설정
            document.getElementById('edit-item-name').value = itemName; // 이름 필드 설정 (수정 허용 가정)
            document.getElementById('edit-item-image-url').value = imageUrl;
            document.getElementById('edit-item-product-url').value = productUrl;

            // 2. 모달 열기
            editModal.classList.add('active');
        }

        // 5.2. 삭제 버튼 (Delete) 처리
        if (e.target.classList.contains('delete-btn')) {
            if (confirm(`제품 ID ${itemId}를 정말 삭제하시겠습니까?`)) {
                deleteItem(itemId, row);
            }
        }
    });

    // --- 6. 폼 제출 처리 ---

    // 6.1. 제품 등록 폼 제출
    addItemForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = {
            brandId: addBrandSelect.value,
            itemName: document.getElementById('add-item-name').value,
            itemImageUrl: document.getElementById('add-item-image-url').value,
            // 구매 URL이 비어있으면 null로 전송
            productUrl: document.getElementById('add-item-product-url').value || null
        };

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.status === 201) { // 201 Created 성공
                alert('새 제품이 성공적으로 등록되었습니다.');
                closeAddModal();
                window.location.reload();
            } else {
                const error = await response.json();
                alert(`제품 등록 실패: ${error.message || response.statusText}`);
            }
        } catch (error) {
            console.error('API Error:', error);
            alert('네트워크 오류가 발생했습니다.');
        }
    });


    // 6.2. 제품 수정 폼 제출
    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const itemId = document.getElementById('edit-item-id').value;

        // 주의: 'edit-item-name'이 수정 가능하다고 가정하고 값을 가져옴
        const data = {
            brandId: document.getElementById('edit-brand-id').value,
            itemName: document.getElementById('edit-item-name').value,
            itemImageUrl: document.getElementById('edit-item-image-url').value,
            productUrl: document.getElementById('edit-item-product-url').value || null
        };

        updateItem(itemId, data);
    });


    // --- 7. API 호출 함수 정의 ---

    // 7.1. 제품 수정 API
    async function updateItem(id, data) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok || response.status === 204) { // 204 No Content도 성공
                alert('제품 정보가 성공적으로 수정되었습니다.');
                closeEditModal();
                window.location.reload();
            } else {
                const error = await response.json();
                alert(`제품 수정 실패: ${error.message || response.statusText}`);
            }
        } catch (error) {
            console.error('API Error:', error);
            alert('네트워크 오류가 발생했습니다.');
        }
    }

    // 7.2. 제품 삭제 API
    async function deleteItem(id, row) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'DELETE'
            });

            if (response.ok || response.status === 204) { // 204 No Content
                alert('제품이 성공적으로 삭제되었습니다.');
                row.remove();
            } else {
                const error = await response.json();
                alert(`제품 삭제 실패: ${error.message || response.statusText}`);
            }
        } catch (error) {
            console.error('API Error:', error);
            alert('네트워크 오류가 발생했습니다.');
        }
    }

    // --- 페이지 로드 시 초기 데이터 로드 호출 ---
    loadInitialData();
});