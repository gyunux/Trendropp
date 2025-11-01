document.addEventListener('DOMContentLoaded', () => {

    // --- 헬퍼 함수 정의 ---
    const setHelperText = (helper, message, color) => {
        if (helper) {
            helper.textContent = message;
            helper.style.color = color;
        }
    };

    // --- 1. 비밀번호 변경 폼 로직 ---
    const passwordForm = document.getElementById('password-change-form');
    if (passwordForm) {
        const newPasswordInput = document.getElementById('new-password');
        const confirmPasswordInput = document.getElementById('new-password-confirm');
        const passwordHelper = document.getElementById('password-helper');
        const confirmHelper = document.getElementById('password-confirm-helper');
        const formErrorMsg = document.getElementById('password-form-error');
        const submitButton = document.getElementById('change-password-btn');

        const validation = {newPasswordValid: false, confirmPasswordValid: false};

        const updateButtonState = () => {
            submitButton.disabled = !(validation.newPasswordValid && validation.confirmPasswordValid);
        };

        newPasswordInput.addEventListener('keyup', () => {
            const value = newPasswordInput.value;
            const hasLower = /[a-z]/.test(value);
            const hasNumber = /\d/.test(value);
            const hasLength = value.length >= 6;
            validation.newPasswordValid = hasLower && hasNumber && hasLength;

            if (value === "") setHelperText(passwordHelper, '6자 이상, 소문자와 숫자 필수', '#888');
            else if (validation.newPasswordValid) setHelperText(passwordHelper, '✓ 안전한 비밀번호입니다.', '#1DB954');
            else setHelperText(passwordHelper, '✗ 6자 이상, 소문자, 숫자를 모두 포함해야 합니다.', '#dc3545');

            confirmPasswordInput.dispatchEvent(new Event('keyup')); // 확인란도 갱신
            updateButtonState();
        });

        confirmPasswordInput.addEventListener('keyup', () => {
            validation.confirmPasswordValid = (newPasswordInput.value === confirmPasswordInput.value) && validation.newPasswordValid;
            if (confirmPasswordInput.value === "") setHelperText(confirmHelper, '', '#888');
            else if (validation.confirmPasswordValid) setHelperText(confirmHelper, '✓ 비밀번호가 일치합니다.', '#1DB954');
            else setHelperText(confirmHelper, '✗ 비밀번호가 일치하지 않습니다.', '#dc3545');
            updateButtonState();
        });

        passwordForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (submitButton.disabled) return;
            formErrorMsg.textContent = '';

            const formData = {
                currentPassword: document.getElementById('current-password').value,
                newPassword: newPasswordInput.value
            };

            try {
                // [백엔드 구현 필요 1]
                const response = await fetch('/api/members/password', {
                    method: 'PATCH', // PUT 또는 PATCH
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || '비밀번호 변경 실패');
                }
                alert('비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.');
                await fetch('/api/members/logout', {method: 'POST'});
                window.location.href = '/';
            } catch (error) {
                setHelperText(formErrorMsg, error.message, '#dc3545');
            }
        });

        updateButtonState(); // 초기 비활성화
    }

    // --- 2. 이메일 변경 폼 로직 ---
    const emailForm = document.getElementById('email-change-form');
    if (emailForm) {
        const newEmailInput = document.getElementById('new-email');
        const emailHelper = document.getElementById('email-helper');
        const emailFormError = document.getElementById('email-form-error');
        const emailSubmitButton = document.getElementById('change-email-btn');

        let isEmailValid = false; // 이메일 유효성(형식+중복) 상태

        newEmailInput.addEventListener('blur', async () => { // 포커스 아웃 시 검사
            const email = newEmailInput.value;
            const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!emailRegex.test(email)) {
                setHelperText(emailHelper, '유효한 이메일 형식이 아닙니다.', '#dc3545');
                isEmailValid = false;
                return;
            }
            try {
                // [백엔드 구현 필요 2]
                const response = await fetch(`/api/members/check-email?email=${encodeURIComponent(email)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(emailHelper, '✓ 사용 가능한 이메일입니다.', '#1DB954');
                    isEmailValid = true;
                } else {
                    setHelperText(emailHelper, '✗ 이미 사용 중인 이메일입니다.', '#dc3545');
                    isEmailValid = false;
                }
            } catch (error) {
                setHelperText(emailHelper, '중복 확인 중 오류', '#dc3545');
                isEmailValid = false;
            }
        });

        emailForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!isEmailValid) {
                alert('이메일 중복 확인을 통과해야 합니다.');
                return;
            }
            emailFormError.textContent = '';

            const formData = {
                newEmail: newEmailInput.value,
                currentPassword: document.getElementById('email-current-password').value
            };

            try {
                // [백엔드 구현 필요 3]
                const response = await fetch('/api/members/email', {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || '이메일 변경 실패');
                }
                alert('이메일이 성공적으로 변경되었습니다.');
                window.location.reload();
            } catch (error) {
                setHelperText(emailFormError, error.message, '#dc3545');
            }
        });
    }

    const deactivateBtn = document.getElementById('deactivate-btn');
    const deleteModal = document.getElementById('delete-account-modal');
    const deleteForm = document.getElementById('delete-account-form');
    const deletePasswordInput = document.getElementById('delete-password');
    const deleteErrorMsg = document.getElementById('delete-form-error');

    // '계정 삭제' 버튼 (마이페이지 본문) 클릭 시 -> 탈퇴 모달 열기
    if (deactivateBtn) {
        deactivateBtn.addEventListener('click', () => {
            // 폼 초기화
            deleteForm.reset();
            deleteErrorMsg.textContent = '';
            // 모달 띄우기
            deleteModal.classList.add('active');
        });
    }

    // 탈퇴 모달의 닫기 버튼/배경 클릭 시 (auth.js와 중복되지만, mypage에서만 쓴다면 여기에 두는게 명확함)
    if (deleteModal) {
        deleteModal.addEventListener('click', (e) => {
            if (e.target.matches('.modal-overlay, .close-btn')) {
                deleteModal.classList.remove('active');
            }
        });
    }

    // [수정] 탈퇴 모달 안의 '계정 삭제' 폼(form) 제출(submit) 시
    if (deleteForm) {
        deleteForm.addEventListener('submit', async (e) => {
            e.preventDefault(); // 새로고침 방지

            const currentPassword = deletePasswordInput.value;
            if (!currentPassword) {
                setHelperText(deleteErrorMsg, '비밀번호를 입력해야 합니다.', '#dc3545');
                return;
            }

            try {
                // 1. 회원 탈퇴(익명화) API 호출
                const deactivateResponse = await fetch('/api/members/me', {
                    method: 'DELETE',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({currentPassword: currentPassword})
                });

                if (!deactivateResponse.ok) {
                    const errorText = await deactivateResponse.text();
                    throw new Error(errorText || '회원 탈퇴에 실패했습니다.');
                }

                alert('회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.');

                // 2. 로그아웃 API 호출
                await fetch('/api/members/logout', {method: 'POST'});

                // 3. 홈으로 이동
                window.location.href = '/';

            } catch (error) {
                // 비밀번호가 틀렸을 경우 등
                setHelperText(deleteErrorMsg, error.message, '#dc3545');
            }
        });
    }
});