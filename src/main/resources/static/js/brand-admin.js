document.addEventListener('DOMContentLoaded', () => {
    // --- 공통 요소 ---
    const tableBody = document.getElementById('brand-table-body');
    const API_BASE_URL = '/api/admin/brands'; // API 경로 변경

    // --- 검색 기능 ---
    const searchInput = document.getElementById('brand-search-input');
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.toLowerCase();
        const rows = tableBody.querySelectorAll('tr');

        rows.forEach(row => {
            const enName = row.querySelector('.brand-en-name')?.textContent.toLowerCase() || '';
            const koName = row.querySelector('.brand-ko-name')?.textContent.toLowerCase() || '';

            if (enName.includes(searchTerm) || koName.includes(searchTerm)) {
                row.style.display = ''; // 보이기
            } else {
                row.style.display = 'none'; // 숨기기
            }
        });
    });

    // --- 새 브랜드 등록 (Create) ---
    const addModal = document.getElementById('add-brand-modal');
    const addBtn = document.getElementById('add-brand-btn');
    const addForm = document.getElementById('add-brand-form');

    addBtn.addEventListener('click', () => addModal.classList.add('active'));
    addModal.addEventListener('click', (e) => {
        if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
            addModal.classList.remove('active');
            addForm.reset();
        }
    });

    addForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = {
            englishName: document.getElementById('add-en-name').value,
            koreanName: document.getElementById('add-ko-name').value,
        };
        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('등록 실패');
            alert('성공적으로 등록되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    });

    // --- 수정 및 삭제 (Update & Delete) ---
    const editModal = document.getElementById('edit-brand-modal');
    const editForm = document.getElementById('edit-brand-form');

    tableBody.addEventListener('click', (e) => {
        const row = e.target.closest('tr');
        if (!row) return;
        const brandId = row.dataset.id;

        // 수정 버튼 클릭 시
        if (e.target.matches('.edit-btn')) {
            document.getElementById('edit-brand-id').value = brandId;
            document.getElementById('edit-en-name').value = row.querySelector('.brand-en-name').textContent;
            document.getElementById('edit-ko-name').value = row.querySelector('.brand-ko-name').textContent;
            editModal.classList.add('active');
        }
        // 삭제 버튼 클릭 시
        if (e.target.matches('.delete-btn')) {
            if (confirm(`브랜드(ID: ${brandId})를 정말로 삭제하시겠습니까?`)) {
                deleteBrand(brandId);
            }
        }
    });

    editModal.addEventListener('click', (e) => {
        if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
            editModal.classList.remove('active');
            editForm.reset();
        }
    });

    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const brandId = document.getElementById('edit-brand-id').value;
        const formData = {
            englishName: document.getElementById('edit-en-name').value,
            koreanName: document.getElementById('edit-ko-name').value,
        };
        try {
            const response = await fetch(`${API_BASE_URL}/${brandId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            if (!response.ok) throw new Error('수정 실패');
            alert('성공적으로 수정되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    });

    async function deleteBrand(brandId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${brandId}`, {
                method: 'DELETE'
            });
            if (!response.ok) throw new Error('삭제 실패');
            alert('성공적으로 삭제되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    }
});