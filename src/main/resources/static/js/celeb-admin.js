document.addEventListener('DOMContentLoaded', () => {

    // --- 1. 초기화 ---
    const API_BASE_URL = '/api/admin/celebs';
    const mainTableBody = document.querySelector('.data-table tbody');

    const addModal = document.getElementById('celeb-modal');
    const addForm = document.getElementById('celeb-form');
    const addCelebBtn = document.getElementById('add-celeb-btn');
    const addProfileImageInput = document.getElementById('profile-image');
    const addFileNameDisplay = document.getElementById('file-name-display');

    const editModal = document.getElementById('edit-celeb-modal');
    const editForm = document.getElementById('edit-celeb-form');
    const editProfileImageInput = document.getElementById('edit-profile-image');
    const editFileNameDisplay = document.getElementById('edit-file-name-display');


    // --- 2. 함수 정의 ---

    // [수정] 수정 폼 열기 함수
    const openEditModal = (celebId, row) => {
        // 폼을 미리 초기화
        editForm.reset();
        editFileNameDisplay.textContent = '현재 이미지 유지';

        // 테이블의 데이터를 가져와 폼에 채워넣기
        document.getElementById('edit-celeb-id').value = celebId;
        document.getElementById('edit-celeb-name').value = row.querySelector('.celeb-name').textContent;
        document.getElementById('edit-instagram-name').value = row.querySelector('.celeb-insta').textContent;

        editModal.classList.add('active');
    };

    // 셀럽 삭제 함수 (기존과 동일)
    const deleteCeleb = async (celebId) => {
        // ...
    };


    // --- 3. 이벤트 리스너 바인딩 ---

    // '새 셀럽 추가' 관련 이벤트
    if (addCelebBtn) {
        addCelebBtn.addEventListener('click', () => addModal.classList.add('active'));
    }
    if (addModal) {
        addModal.addEventListener('click', (e) => {
            if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
                addModal.classList.remove('active');
                addForm.reset();
                if (addFileNameDisplay) addFileNameDisplay.textContent = '선택된 파일 없음';
            }
        });
    }
    if (addProfileImageInput && addFileNameDisplay) {
        addProfileImageInput.addEventListener('change', () => {
            addFileNameDisplay.textContent = addProfileImageInput.files.length > 0 ? addProfileImageInput.files[0].name : '선택된 파일 없음';
        });
    }
    if (addForm) {
        addForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData();
            formData.append('name', document.getElementById('celeb-name').value);
            formData.append('instagramName', document.getElementById('instagram-name').value);
            if (addProfileImageInput.files.length > 0) {
                formData.append('profileImage', addProfileImageInput.files[0]);
            }
            // ... fetch 로직 ...
        });
    }

    // 테이블 내 '수정/삭제' 버튼 클릭 이벤트
    if (mainTableBody) {
        mainTableBody.addEventListener('click', (e) => {
            const row = e.target.closest('tr');
            if (!row) return;
            const celebId = row.dataset.id;

            if (e.target.matches('.edit-btn')) openEditModal(celebId, row);
            if (e.target.matches('.delete-btn')) {
                if (confirm(`정말로 이 셀럽(ID: ${celebId})을 삭제하시겠습니까?`)) deleteCeleb(celebId);
            }
        });
    }

    // '수정' 모달 관련 이벤트
    if (editModal) {
        editModal.addEventListener('click', (e) => {
            if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
                editModal.classList.remove('active');
            }
        });
    }
    if (editProfileImageInput && editFileNameDisplay) {
        editProfileImageInput.addEventListener('change', () => {
            editFileNameDisplay.textContent = editProfileImageInput.files.length > 0 ? editProfileImageInput.files[0].name : '현재 이미지 유지';
        });
    }
    // [수정] 수정 폼 제출 이벤트
    if (editForm) {
        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const celebId = document.getElementById('edit-celeb-id').value;

            const formData = new FormData();
            formData.append('name', document.getElementById('edit-celeb-name').value);
            formData.append('instagramName', document.getElementById('edit-instagram-name').value);

            if (editProfileImageInput.files.length > 0) {
                formData.append('profileImage', editProfileImageInput.files[0]);
            }

            try {
                // [참고] FormData를 PUT으로 보내려면 POST + _method=PUT 방식을 사용하거나 서버 설정을 변경해야 할 수 있습니다.
                // 여기서는 일단 PUT으로 보냅니다.
                const response = await fetch(`${API_BASE_URL}/${celebId}`, {
                    method: 'PUT',
                    body: formData
                });
                if (!response.ok) throw new Error('수정에 실패했습니다.');
                alert('성공적으로 수정되었습니다.');
                window.location.reload();
            } catch (error) {
                alert(error.message);
            }
        });
    }
});