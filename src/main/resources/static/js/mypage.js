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
});