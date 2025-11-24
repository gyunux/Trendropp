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
        document.getElementById('edit-celeb-name-ko').value = row.dataset.nameKo; // '제니'
        document.getElementById('edit-celeb-name-en').value = row.dataset.nameEn; // 'Jennie'
        document.getElementById('edit-instagram-name').value = row.querySelector('.celeb-insta').textContent;
        editModal.classList.add('active');
    };

    // 셀럽 삭제 함수 (기존과 동일)
    const deleteCeleb = async (celebId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${celebId}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('삭제에 실패했습니다. 상태 코드: ' + response.status);
            }

            alert('성공적으로 삭제되었습니다.');
            window.location.reload(); // 리스트 갱신을 위해 새로고침
        } catch (error) {
            console.error(">>> 삭제 중 에러 발생:", error);
            alert(error.message);
        }
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
            // [진단 1] 이 로그가 콘솔에 찍히는지 확인 (이벤트 리스너가 동작하는지)
            console.log(">>> '새 셀럽 등록' submit 이벤트 발생!");

            // [진단 2] profileImageInput 변수가 올바른지 확인
            if (!addProfileImageInput || !addProfileImageInput.files) {
                console.error("### 오류: profileImageInput 요소를 찾을 수 없습니다!");
                alert("파일 입력 요소를 찾을 수 없습니다. HTML ID를 확인하세요.");
                return;
            }
            if (addProfileImageInput.files.length === 0) {
                console.warn("### 경고: 프로필 이미지가 선택되지 않았습니다.");
                // 여기서 alert를 띄우거나 return; 할 수 있습니다. (required 속성이 이미 있으므로 필수는 아님)
            }


            const formData = new FormData();
            formData.append('nameKo', document.getElementById('celeb-name-ko').value);
            formData.append('nameEn', document.getElementById('celeb-name-en').value);
            formData.append('instagramName', document.getElementById('instagram-name').value);
            formData.append('profileImage', addProfileImageInput.files[0]);

            // [진단 3] FormData 내용 확인
            console.log(">>> FormData 내용:", {
                name: formData.get('name'),
                instagramName: formData.get('instagramName'),
                profileImage: formData.get('profileImage') ? formData.get('profileImage').name : '파일 없음'
            });

            try {
                // [진단 4] Fetch 요청 직전 로그
                console.log(">>> API 호출 시도:", API_BASE_URL);

                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    body: formData
                });

                // [진단 5] Fetch 응답 확인
                console.log(">>> API 응답 상태:", response.status);

                if (!response.ok) throw new Error('등록에 실패했습니다. 상태 코드: ' + response.status);
                alert('성공적으로 등록되었습니다.');
                window.location.reload();
            } catch (error) {
                // [진단 6] Fetch 또는 그 이후 오류 확인
                console.error(">>> '새 셀럽 등록' API 호출 중 에러 발생:", error);
                alert(error.message);
            }
        });
    } else {
        // [진단 0] 만약 addForm 자체가 null이라면 이 로그가 찍힘
        console.error("### 심각: addForm (ID: celeb-form) 요소를 찾지 못했습니다! ###");
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
            formData.append('nameKo', document.getElementById('edit-celeb-name-ko').value);
            formData.append('nameEn', document.getElementById('edit-celeb-name-en').value);
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