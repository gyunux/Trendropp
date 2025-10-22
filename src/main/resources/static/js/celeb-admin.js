document.addEventListener('DOMContentLoaded', () => {

    // --- 1. 초기화: 모든 DOM 요소를 한번에 찾기 ---
    const API_BASE_URL = '/api/admin/celebs';
    const mainTableBody = document.querySelector('.data-table tbody');

    // '새 셀럽 등록' 관련 요소
    const addCelebBtn = document.getElementById('add-celeb-btn');
    const addModal = document.getElementById('celeb-modal');
    const addForm = document.getElementById('celeb-form');
    const profileImageInput = document.getElementById('profile-image');
    const fileNameDisplay = document.getElementById('file-name-display');

    // '셀럽 수정' 관련 요소
    const editModal = document.getElementById('edit-celeb-modal');
    const editForm = document.getElementById('edit-celeb-form');


    // --- 2. 함수 정의 ---

    // 수정 폼을 여는 함수
    const openEditModal = (celebId, row) => {
        console.log('>>> 수정 버튼 로직을 실행합니다.');
        document.getElementById('edit-celeb-id').value = celebId;
        document.getElementById('edit-celeb-name').value = row.querySelector('.celeb-name').textContent;
        document.getElementById('edit-profile-image-url').value = row.querySelector('.profile-img').src;
        document.getElementById('edit-instagram-name').value = row.querySelector('td:nth-child(3)').textContent;
        editModal.classList.add('active');
    };

    // 셀럽 삭제 API를 호출하는 함수
    const deleteCeleb = async (celebId) => {
        console.log(`삭제 함수 호출! 대상 ID: ${celebId}`);
        try {
            const response = await fetch(`${API_BASE_URL}/${celebId}`, { method: 'DELETE' });
            if (!response.ok) throw new Error('삭제에 실패했습니다. 상태 코드: ' + response.status);
            alert('성공적으로 삭제되었습니다.');
            window.location.reload();
        } catch (error) {
            console.error(`ID ${celebId} 셀럽 삭제 API 호출 중 에러 발생:`, error);
            alert(error.message);
        }
    };


    // --- 3. 이벤트 리스너 바인딩 ---

    // '새 셀럽 추가' 버튼 클릭 -> 모달 열기
    if (addCelebBtn) {
        addCelebBtn.addEventListener('click', () => {
            console.log("새 셀럽 등록 버튼 클릭! 모달을 엽니다.");
            addModal.classList.add('active');
        });
    }

    // '새 셀럽 추가' 모달 닫기 (X, 취소, 바깥 클릭)
    if (addModal) {
        addModal.addEventListener('click', (e) => {
            if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
                console.log("새 셀럽 등록 모달을 닫습니다.");
                addModal.classList.remove('active');
                addForm.reset();
                if (fileNameDisplay) fileNameDisplay.textContent = '선택된 파일 없음';
            }
        });
    }

    // '파일 선택' 시 파일 이름 표시
    if (profileImageInput && fileNameDisplay) {
        profileImageInput.addEventListener('change', () => {
            if (profileImageInput.files.length > 0) {
                fileNameDisplay.textContent = profileImageInput.files[0].name;
            } else {
                fileNameDisplay.textContent = '선택된 파일 없음';
            }
        });
    }

    // '새 셀럽 추가' 폼 제출 (FormData 사용)
    if (addForm) {
        addForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData();
            formData.append('name', document.getElementById('celeb-name').value);
            formData.append('instagramName', document.getElementById('instagram-name').value);
            formData.append('profileImage', profileImageInput.files[0]);

            try {
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    body: formData
                });
                if (!response.ok) throw new Error('등록에 실패했습니다. 상태 코드: ' + response.status);
                alert('성공적으로 등록되었습니다.');
                window.location.reload();
            } catch (error) {
                console.error("새 셀럽 등록 API 호출 중 에러 발생:", error);
                alert(error.message);
            }
        });
    }

    // 테이블 내 '수정/삭제' 버튼 클릭 이벤트 (이벤트 위임)
    if (mainTableBody) {
        mainTableBody.addEventListener('click', (e) => {
            const row = e.target.closest('tr');
            if (!row) return;

            const celebId = row.dataset.id;

            if (e.target.matches('.edit-btn')) {
                openEditModal(celebId, row);
            }

            if (e.target.matches('.delete-btn')) {
                if (confirm(`정말로 이 셀럽(ID: ${celebId})을 삭제하시겠습니까?`)) {
                    deleteCeleb(celebId);
                }
            }
        });
    }

    // '수정' 모달 닫기
    if (editModal) {
        editModal.addEventListener('click', (e) => {
            if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
                console.log("수정 모달을 닫습니다.");
                editModal.classList.remove('active');
                editForm.reset();
            }
        });
    }

    // '수정' 폼 제출 (JSON 사용 - 기존 로직 유지)
    if (editForm) {
        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const celebId = document.getElementById('edit-celeb-id').value;
            const updatedData = {
                profileImageUrl: document.getElementById('edit-profile-image-url').value,
                instagramName: document.getElementById('edit-instagram-name').value
            };

            try {
                const response = await fetch(`${API_BASE_URL}/${celebId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedData)
                });
                if (!response.ok) throw new Error('수정에 실패했습니다. 상태 코드: ' + response.status);
                alert('성공적으로 수정되었습니다.');
                window.location.reload();
            } catch (error) {
                console.error(`ID ${celebId} 셀럽 수정 API 호출 중 에러 발생:`, error);
                alert(error.message);
            }
        });
    }
});