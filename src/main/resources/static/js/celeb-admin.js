document.addEventListener('DOMContentLoaded', () => {

    // --- 공통 변수 ---
    const mainTableBody = document.querySelector('.celeb-table tbody');
    // ▼▼▼ API 기본 URL을 상수로 관리합니다. ▼▼▼
    const API_BASE_URL = '/api/admin/celebs';

    // --- 새 셀럽 등록 관련 ---
    const addModal = document.getElementById('celeb-modal');
    const addCelebBtn = document.getElementById('add-celeb-btn');
    const addForm = document.getElementById('celeb-form');

    if (addCelebBtn) {
        addCelebBtn.addEventListener('click', () => {
            console.log("새 셀럽 등록 버튼 클릭! 모달을 엽니다.");
            addModal.classList.add('active');
        });
    }

    if (addModal) {
        addModal.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal-overlay') || e.target.classList.contains('close-btn') || e.target.classList.contains('cancel-btn')) {
                console.log("새 셀럽 등록 모달을 닫습니다.");
                addModal.classList.remove('active');
                addForm.reset();
            }
        });
    }

    if (addForm) {
        addForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            console.log("새 셀럽 등록 폼 제출(저장) 이벤트 발생!");
            const formData = {
                name: document.getElementById('celeb-name').value,
                profileImageUrl: document.getElementById('profile-image-url').value,
                instagramName: document.getElementById('instagram-name').value
            };
            console.log("전송할 데이터:", formData);

            try {
                // ▼▼▼ API_BASE_URL 사용 ▼▼▼
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
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

    // --- 수정 및 삭제 관련 ---
    const editModal = document.getElementById('edit-celeb-modal');
    const editForm = document.getElementById('edit-celeb-form');

    if (mainTableBody) {
        mainTableBody.addEventListener('click', (e) => {

            const target = e.target;
            console.log('실제로 클릭된 요소 (e.target):', target);

            // 버튼 클래스 확인
            const isEditButton = target.classList.contains('edit-btn');
            console.log("클릭된 요소가 '.edit-btn' 인가?", isEditButton);
            const isDeleteButton = target.classList.contains('delete-btn');
            console.log("클릭된 요소가 '.delete-btn' 인가?", isDeleteButton);

            const row = target.closest('tr');
            if (!row) {
                console.log("클릭된 요소의 부모 <tr>을 찾지 못했습니다. 이벤트를 무시합니다.");
                return;
            }
            console.log("클릭된 요소의 부모 <tr>을 찾았습니다. data-id:", row.dataset.id);

            const celebId = row.dataset.id;

            // 수정 버튼 클릭 시
            if (isEditButton) {
                console.log('>>> 수정 버튼 로직을 실행합니다.');
                document.getElementById('edit-celeb-id').value = celebId;
                document.getElementById('edit-celeb-name').value = row.querySelector('.celeb-name').textContent;
                document.getElementById('edit-profile-image-url').value = row.querySelector('.profile-img').src;
                document.getElementById('edit-instagram-name').value = row.querySelector('td:nth-child(3)').textContent;
                editModal.classList.add('active');
            }

            // 삭제 버튼 클릭 시
            if (isDeleteButton) {
                console.log('>>> 삭제 버튼 로직을 실행합니다.');
                if (confirm(`정말로 이 셀럽(ID: ${celebId})을 삭제하시겠습니까?`)) {
                    deleteCeleb(celebId);
                }
            }
        });
    } else {
        console.error("### 심각: mainTableBody 요소를 찾지 못했습니다. HTML의 클래스 이름이나 구조를 확인하세요. ###");
    }

    // 수정 모달 닫기
    if (editModal) {
        editModal.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal-overlay') || e.target.classList.contains('close-btn') || e.target.classList.contains('cancel-btn')) {
                console.log("수정 모달을 닫습니다.");
                editModal.classList.remove('active');
                editForm.reset();
            }
        });
    }

    // 수정 폼 제출(저장) 이벤트 처리
    if (editForm) {
        editForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const celebId = document.getElementById('edit-celeb-id').value;
            console.log(`수정 폼 제출! 대상 ID: ${celebId}`);
            const updatedData = {
                profileImageUrl: document.getElementById('edit-profile-image-url').value,
                instagramName: document.getElementById('edit-instagram-name').value
            };
            console.log("수정할 데이터:", updatedData);

            try {
                // ▼▼▼ API_BASE_URL 사용 ▼▼▼
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

    // 셀럽 삭제 API 호출 함수
    const deleteCeleb = async (celebId) => {
        console.log(`삭제 함수 호출! 대상 ID: ${celebId}`);
        try {
            // ▼▼▼ API_BASE_URL 사용 ▼▼▼
            const response = await fetch(`${API_BASE_URL}/${celebId}`, {
                method: 'DELETE'
            });
            if (!response.ok) throw new Error('삭제에 실패했습니다. 상태 코드: ' + response.status);
            alert('성공적으로 삭제되었습니다.');
            window.location.reload();
        } catch (error) {
            console.error(`ID ${celebId} 셀럽 삭제 API 호출 중 에러 발생:`, error);
            alert(error.message);
        }
    };
});