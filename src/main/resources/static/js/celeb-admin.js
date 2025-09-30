document.addEventListener('DOMContentLoaded', () => {

    // 필요한 DOM 요소들을 변수에 할당
    const addCelebBtn = document.getElementById('add-celeb-btn');
    const modal = document.getElementById('celeb-modal');
    const closeModalBtn = document.getElementById('close-modal-btn');
    const cancelBtn = document.getElementById('cancel-btn');
    const celebForm = document.getElementById('celeb-form');

    // 모달을 여는 함수
    const openModal = () => {
        modal.classList.add('active');
    };

    // 모달을 닫는 함수
    const closeModal = () => {
        modal.classList.remove('active');
        celebForm.reset(); // 모달이 닫힐 때 폼 내용을 초기화
    };

    // 이벤트 리스너 할당
    if (addCelebBtn) {
        addCelebBtn.addEventListener('click', openModal);
    }
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', closeModal);
    }
    if (cancelBtn) {
        cancelBtn.addEventListener('click', closeModal);
    }

    // 모달의 회색 배경을 클릭했을 때 닫히도록 설정
    if (modal) {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                closeModal();
            }
        });
    }

    // 폼 제출(저장) 이벤트 처리
    if (celebForm) {
        celebForm.addEventListener('submit', (event) => {
            event.preventDefault(); // 폼의 기본 제출(페이지 새로고침) 동작을 막음

            // 폼에서 데이터 추출
            const formData = {
                name: document.getElementById('celeb-name').value,
                profileImageUrl: document.getElementById('profile-image-url').value,
                instagramUsername: document.getElementById('instagram-name').value
            };

            // ⭐ 백엔드 개발자를 위한 API 계약서 (JSON Payload)
            console.log('--- 백엔드로 전송될 JSON 데이터 ---');
            console.log(JSON.stringify(formData, null, 2));

            // 실제 API 호출 로직 (fetch 사용)
            fetch('/api/v1/admin/celebs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            })
                .then(response => {
                    if (response.status === 201) { // 201 Created 응답을 확인
                        return response.json();
                    }
                    throw new Error('셀럽 등록에 실패했습니다.');
                })
                .then(data => {
                    console.log('성공:', data);
                    alert('셀럽이 성공적으로 등록되었습니다!');
                    closeModal();
                    // 성공 후 목록을 새로고침하기 위해 페이지를 다시 로드
                    window.location.reload();
                })
                .catch((error) => {
                    console.error('오류:', error);
                    alert(error.message);
                });
        });
    }
});