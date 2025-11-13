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

            if (value === "") setHelperText(passwordHelper, window.i18n.validation.passwordDefault, '#888');
            else if (validation.newPasswordValid) setHelperText(passwordHelper, window.i18n.validation.passwordSecure, '#1DB954');
            else setHelperText(passwordHelper, window.i18n.validation.passwordInvalid, '#dc3545');

            confirmPasswordInput.dispatchEvent(new Event('keyup'));
            updateButtonState();
        });

        confirmPasswordInput.addEventListener('keyup', () => {
            validation.confirmPasswordValid = (newPasswordInput.value === confirmPasswordInput.value) && validation.newPasswordValid;

            if (confirmPasswordInput.value === "") setHelperText(confirmHelper, '', '#888');
            else if (validation.confirmPasswordValid) setHelperText(confirmHelper, window.i18n.validation.passwordConfirmMatch, '#1DB954');
            else setHelperText(confirmHelper, window.i18n.validation.passwordConfirmMismatch, '#dc3545');
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
                const response = await fetch('/api/members/password', {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || window.i18n.alert.passwordChangeFailed);
                }

                await Swal.fire({
                    title: window.i18n.alert.passwordChangeSuccess,
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                });

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

        let isEmailValid = false;

        newEmailInput.addEventListener('blur', async () => {
            const email = newEmailInput.value;
            const emailRegex = /^[a-zA-Z0-T9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!emailRegex.test(email)) {
                setHelperText(emailHelper, window.i18n.validation.emailInvalid, '#dc3545');
                isEmailValid = false;
                return;
            }
            try {
                const response = await fetch(`/api/members/check-email?email=${encodeURIComponent(email)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(emailHelper, window.i18n.validation.emailAvailable, '#1DB954');
                    isEmailValid = true;
                } else {
                    setHelperText(emailHelper, window.i18n.validation.emailTaken, '#dc3545');
                    isEmailValid = false;
                }
            } catch (error) {
                setHelperText(emailHelper, window.i18n.validation.checkError, '#dc3545');
                isEmailValid = false;
            }
        });

        emailForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!isEmailValid) {
                Swal.fire({
                    title: window.i18n.alert.emailCheckRequired,
                    icon: 'warning'
                });
                return;
            }
            emailFormError.textContent = '';

            const formData = {
                newEmail: newEmailInput.value,
                currentPassword: document.getElementById('email-current-password').value
            };

            try {
                const response = await fetch('/api/members/email', {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || window.i18n.alert.emailChangeFailed);
                }

                await Swal.fire({
                    title: window.i18n.alert.emailChangeSuccess,
                    icon: 'success',
                    timer: 1500,
                    showConfirmButton: false
                });

                window.location.reload();
            } catch (error) {
                setHelperText(emailFormError, error.message, '#dc3545');
            }
        });
    }

    // --- 3. 회원 탈퇴 로직 ---
    const deactivateBtn = document.getElementById('deactivate-btn');
    const deleteModal = document.getElementById('delete-account-modal');
    const deleteForm = document.getElementById('delete-account-form');
    const deletePasswordInput = document.getElementById('delete-password');
    const deleteErrorMsg = document.getElementById('delete-form-error');

    // [★ 수정됨 ★] 모달 열기 로직 추가
    if (deactivateBtn) {
        deactivateBtn.addEventListener('click', () => {
            // 폼 초기화
            if (deleteForm) deleteForm.reset();
            if (deleteErrorMsg) deleteErrorMsg.textContent = '';
            // 모달 띄우기
            if (deleteModal) deleteModal.classList.add('active');
        });
    }

    // [★ 수정됨 ★] 모달 닫기 로직 삭제
    // (auth.js의 공통 닫기 로직이 처리하므로 mypage.js에서는 필요 없음)

    // 탈퇴 폼 제출(submit) 로직
    if (deleteForm) {
        deleteForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const currentPassword = deletePasswordInput.value;
            if (!currentPassword) {
                setHelperText(deleteErrorMsg, window.i18n.validation.passwordEmpty, '#dc3545');
                return;
            }

            try {
                const deactivateResponse = await fetch('/api/members/me', {
                    method: 'DELETE',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({currentPassword: currentPassword})
                });

                if (!deactivateResponse.ok) {
                    const errorText = await deactivateResponse.text();
                    throw new Error(errorText || window.i18n.alert.deactivateFailed);
                }

                await Swal.fire({
                    title: window.i18n.alert.deactivateSuccess,
                    icon: 'success'
                });

                // (탈퇴 성공 시 로그아웃도 자동으로 처리되므로, 별도 로그아웃 fetch는 불필요)
                window.location.href = '/';

            } catch (error) {
                setHelperText(deleteErrorMsg, error.message, '#dc3545');
            }
        });
    }
});