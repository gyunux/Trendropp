document.addEventListener('DOMContentLoaded', () => {

    // --- 1. 모달 요소 찾기 ---
    const loginModal = document.getElementById('login-modal');
    const signupModal = document.getElementById('signup-modal');
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');

    // --- 2. 모달 및 공통 버튼 이벤트 처리 (이벤트 위임) ---
    document.body.addEventListener('click', async (e) => {

        // ... (모달 열기/닫기 로직은 변경 없음) ...
        if (e.target.matches('#open-login-modal-btn')) {
            e.preventDefault();
            if (loginModal) loginModal.classList.add('active');
        }
        if (e.target.matches('#open-signup-modal-btn')) {
            e.preventDefault();
            if (signupModal) signupModal.classList.add('active');
        }
        if (e.target.matches('#show-signup-modal-link')) {
            e.preventDefault();
            if (loginModal) loginModal.classList.remove('active');
            if (signupModal) signupModal.classList.add('active');
        }
        if (e.target.matches('#show-login-modal-link')) {
            e.preventDefault();
            if (signupModal) signupModal.classList.remove('active');
            if (loginModal) loginModal.classList.add('active');
        }
        if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
            // 클릭된 요소(e.target)에서 가장 가까운 부모 .modal-overlay를 찾습니다.
            const modalToClose = e.target.closest('.modal-overlay');

            // 찾았다면, 그 모달을 닫습니다.
            if (modalToClose) {
                modalToClose.classList.remove('active');
            }
        }

        // '로그아웃' 버튼 (헤더)
        if (e.target.matches('#logout-btn')) {
            e.preventDefault();
            handleLogout(); // [★수정됨★] 확인창 없이 즉시 로그아웃 함수 호출
        }

        // ... (찜하기, 보호된 링크 로직은 토스트 방식 그대로 유지) ...
        if (e.target.matches('#protected-likes-link, #protected-mypage-link')) {
            e.preventDefault();
            Swal.fire({
                text: window.i18n.alert.loginRequired,
                icon: 'info',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
        const likeButton = e.target.closest('.like-button');
        if (likeButton) {
            e.preventDefault();
            const isLoggedIn = !!document.getElementById('logout-btn');

            if (!isLoggedIn) {
                Swal.fire({
                    text: window.i18n.alert.loginRequired,
                    icon: 'info',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
                return;
            }

            // ... (찜하기 API 호출 로직) ...
            const contentId = likeButton.dataset.contentId;
            const isLiked = likeButton.classList.contains('active');
            const method = isLiked ? 'DELETE' : 'POST';
            const apiUri = `/api/contents/${contentId}/like`;

            try {
                const response = await fetch(apiUri, {method: method});
                if (!response.ok) {
                    throw new Error(window.i18n.alert.likeFailed);
                }
                likeButton.classList.toggle('active');
            } catch (error) {
                console.error('Like error:', error);
                Swal.fire({
                    text: error.message,
                    icon: 'error',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true
                });
            }
        }
    }); // <body> 클릭 이벤트 리스너 끝

    // --- 3. 로그인 폼 제출 (Fetch API) ---
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            // ... (로그인 폼 로직은 변경 없음) ...
            e.preventDefault();
            const userId = document.getElementById('login-userId').value;
            const password = document.getElementById('login-password').value;
            const errorMsg = document.getElementById('login-error-msg');
            errorMsg.textContent = '';
            const formData = new URLSearchParams();
            formData.append('userId', userId);
            formData.append('password', password);
            try {
                const response = await fetch('/api/members/login', {
                    method: 'POST',
                    body: formData
                });
                if (!response.ok) {
                    throw new Error(window.i18n.alert.loginFailed);
                }
                window.location.reload();
            } catch (error) {
                errorMsg.textContent = error.message;
            }
        });
    }

    // --- 4. 로그아웃 처리 (Fetch API) ---
    const handleLogout = async () => {
        try {
            const response = await fetch('/api/members/logout', {method: 'POST'});

            if (!response.ok) {
                throw new Error(window.i18n.alert.logoutFailed);
            }

            // [변경] 성공 알림(Swal.fire) 제거
            window.location.reload(); // 알림 없이 바로 새로고침

        } catch (error) {
            // [변경 없음] 실패 시에는 토스트 알림을 띄우는 것이 좋습니다.
            Swal.fire({
                text: error.message,
                icon: 'error',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
        }
    };

    // --- 5. 회원가입 폼이 존재하면 유효성 검사 로직 실행 ---
    if (signupForm) {
        setupSignupFormValidation(signupForm, loginModal, signupModal);
    }
});


/**
 * 회원가입 폼의 모든 유효성 검사 및 제출 로직을 설정합니다.
 */
function setupSignupFormValidation(signupForm, loginModal, signupModal) {
    // ... (이 함수는 토스트 방식으로 수정된 것 외에 변경 없음) ...
    const userIdInput = document.getElementById('userId');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const nameInput = document.getElementById('name');
    const submitButton = signupForm.querySelector('button[type="submit"]');

    const userIdHelper = document.getElementById('userId-helper');
    const emailHelper = document.getElementById('email-helper');
    const passwordHelper = document.getElementById('password-helper');

    const validationStatus = {userId: false, email: false, password: false, name: false};

    const updateSubmitButtonState = () => {
        const allValid = Object.values(validationStatus).every(status => status === true);
        submitButton.disabled = !allValid;
    };

    const setHelperText = (helper, message, color) => {
        if (helper) {
            helper.textContent = message;
            helper.style.color = color;
        }
    };

    if (nameInput) {
        nameInput.addEventListener('keyup', () => {
            validationStatus.name = nameInput.value.trim().length > 0;
            updateSubmitButtonState();
        });
    }

    if (passwordInput && passwordHelper) {
        passwordInput.addEventListener('keyup', () => {
            const value = passwordInput.value;
            const hasLower = /[a-z]/.test(value);
            const hasNumber = /\d/.test(value);
            const hasLength = value.length >= 6;

            if (value === "") {
                setHelperText(passwordHelper, window.i18n.validation.passwordDefault, '#888');
                validationStatus.password = false;
            } else if (hasLower && hasNumber && hasLength) {
                setHelperText(passwordHelper, window.i18n.validation.passwordSecure, '#1DB954');
                validationStatus.password = true;
            } else {
                setHelperText(passwordHelper, window.i18n.validation.passwordInvalid, '#dc3545');
                validationStatus.password = false;
            }
            updateSubmitButtonState();
        });
    }

    if (userIdInput && userIdHelper) {
        userIdInput.addEventListener('blur', async (e) => {
            const userId = e.target.value.trim();
            if (userId.length < 4) {
                setHelperText(userIdHelper, window.i18n.validation.userIdLength, '#dc3545');
                validationStatus.userId = false;
                updateSubmitButtonState();
                return;
            }
            try {
                const response = await fetch(`/api/members/check-userid?userId=${encodeURIComponent(userId)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(userIdHelper, window.i18n.validation.userIdAvailable, '#1DB954');
                    validationStatus.userId = true;
                } else {
                    setHelperText(userIdHelper, window.i18n.validation.userIdTaken, '#dc3545');
                    validationStatus.userId = false;
                }
            } catch (error) {
                setHelperText(userIdHelper, window.i18n.validation.checkError, '#dc3545');
                validationStatus.userId = false;
            }
            updateSubmitButtonState();
        });
    }

    if (emailInput && emailHelper) {
        emailInput.addEventListener('blur', async (e) => {
            const email = e.target.value.trim();
            const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!emailRegex.test(email)) {
                setHelperText(emailHelper, window.i18n.validation.emailInvalid, '#dc3545');
                validationStatus.email = false;
                updateSubmitButtonState();
                return;
            }
            try {
                const response = await fetch(`/api/members/check-email?email=${encodeURIComponent(email)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(emailHelper, window.i18n.validation.emailAvailable, '#1DB954');
                    validationStatus.email = true;
                } else {
                    setHelperText(emailHelper, window.i18n.validation.emailTaken, '#dc3545');
                    validationStatus.email = false;
                }
            } catch (error) {
                setHelperText(emailHelper, window.i18n.validation.checkError, '#dc3545');
                validationStatus.email = false;
            }
            updateSubmitButtonState();
        });
    }

    signupForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (submitButton.disabled) {
            Swal.fire({
                text: window.i18n.alert.signupCheckForm,
                icon: 'warning',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            return;
        }

        const formData = {
            userId: userIdInput.value,
            password: passwordInput.value,
            name: nameInput.value,
            email: emailInput.value
        };

        try {
            const response = await fetch('/api/members/signup', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                if (response.status === 400) {
                    const errors = await response.json();
                    const firstErrorMessage = errors[Object.keys(errors)[0]];
                    Swal.fire({
                        title: window.i18n.alert.signupErrorTitle,
                        text: `${window.i18n.alert.signupErrorPrefix}${firstErrorMessage}`,
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 4000,
                        timerProgressBar: true
                    });
                } else {
                    Swal.fire({
                        text: window.i18n.alert.signupFailed,
                        icon: 'error',
                        toast: true,
                        position: 'top-end',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                }
                throw new Error('Signup failed');
            }

            await Swal.fire({
                text: window.i18n.alert.signupSuccess,
                icon: 'success',
                toast: true,
                position: 'top-end',
                showConfirmButton: false,
                timer: 1500
            });

            signupForm.reset();
            setHelperText(document.getElementById('userId-helper'), '', '#888');
            setHelperText(document.getElementById('email-helper'), '', '#888');
            setHelperText(document.getElementById('password-helper'), window.i18n.validation.passwordDefault, '#888');

            if (signupModal) signupModal.classList.remove('active');
            if (loginModal) loginModal.classList.add('active');

        } catch (error) {
            console.error("Signup submit failed:", error);
        }
    });

    updateSubmitButtonState();
}