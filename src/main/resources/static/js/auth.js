document.addEventListener('DOMContentLoaded', () => {

    // --- 1. 모달 요소 찾기 ---
    const loginModal = document.getElementById('login-modal');
    const signupModal = document.getElementById('signup-modal');
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');

    // --- 2. 모달 열기/닫기/전환 이벤트 처리 (이벤트 위임) ---
    document.body.addEventListener('click', (e) => {
        // '로그인' 버튼 (헤더)
        if (e.target.matches('#open-login-modal-btn')) {
            e.preventDefault();
            loginModal.classList.add('active');
        }
        // '회원가입' 버튼 (헤더)
        if (e.target.matches('#open-signup-modal-btn')) {
            e.preventDefault();
            signupModal.classList.add('active');
        }
        // '회원가입' 링크 (로그인 모달 안)
        if (e.target.matches('#show-signup-modal-link')) {
            e.preventDefault();
            loginModal.classList.remove('active');
            signupModal.classList.add('active');
        }
        // '로그인' 링크 (회원가입 모달 안)
        if (e.target.matches('#show-login-modal-link')) {
            e.preventDefault();
            signupModal.classList.remove('active');
            loginModal.classList.add('active');
        }
        // 모달 닫기 (X 버튼, 취소 버튼, 바깥 영역)
        if (e.target.matches('.modal-overlay, .close-btn, .cancel-btn')) {
            if (loginModal) loginModal.classList.remove('active');
            if (signupModal) signupModal.classList.remove('active');
        }
        // '로그아웃' 버튼 (헤더)
        if (e.target.matches('#logout-btn')) {
            e.preventDefault();
            if (confirm('로그아웃 하시겠습니까?')) {
                handleLogout();
            }
        }
    });

    // --- 3. 로그인 폼 제출 (Fetch API) ---
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const userId = document.getElementById('login-userId').value;
            const password = document.getElementById('login-password').value;
            const errorMsg = document.getElementById('login-error-msg');
            errorMsg.textContent = ''; // 이전 에러 메시지 초기화

            // [핵심 수정] 1: JSON 객체 대신 URLSearchParams 객체 사용
            // 이 객체는 'userId=kdk942&password=1234' 형태의 폼 데이터를 만듭니다.
            const formData = new URLSearchParams();
            formData.append('userId', userId); // .usernameParameter("userId")와 일치
            formData.append('password', password);

            try {
                const response = await fetch('/api/members/login', { // .loginProcessingUrl()과 일치
                    method: 'POST',
                    // [핵심 수정] 2: Content-Type 헤더 삭제 (브라우저가 자동으로 'x-www-form-urlencoded'로 설정)
                    body: formData // [핵심 수정] 3: JSON.stringify 대신 formData 객체 전달
                });

                if (!response.ok) { // 401 Unauthorized 등
                    throw new Error('아이디 또는 비밀번호가 올바르지 않습니다.');
                }

                // 200 OK (로그인 성공)
                window.location.reload(); // 페이지 새로고침 (로그인 상태 반영)

            } catch (error) {
                errorMsg.textContent = error.message;
            }
        });
    }

    // --- 4. 로그아웃 처리 (Fetch API) ---
    const handleLogout = async () => {
        try {
            const response = await fetch('/api/members/logout', {method: 'POST'});
            if (!response.ok) throw new Error('로그아웃 실패');
            alert('로그아웃되었습니다.');
            window.location.reload(); // 페이지 새로고침
        } catch (error) {
            alert(error.message);
        }
    };

    // --- 5. 회원가입 폼이 존재하면 유효성 검사 로직 실행 ---
    if (signupForm) {
        setupSignupFormValidation(signupForm, loginModal, signupModal);
    }
});


/**
 * 회원가입 폼의 모든 유효성 검사 및 제출 로직을 설정합니다.
 * (이전 코드와 동일, 'userId'로 수정된 최종본)
 */
function setupSignupFormValidation(signupForm, loginModal, signupModal) {
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
                setHelperText(passwordHelper, '6자 이상, 소문자와 숫자 필수', '#888');
                validationStatus.password = false;
            } else if (hasLower && hasNumber && hasLength) {
                setHelperText(passwordHelper, '✓ 안전한 비밀번호입니다.', '#1DB954');
                validationStatus.password = true;
            } else {
                setHelperText(passwordHelper, '✗ 6자 이상, 소문자, 숫자를 모두 포함해야 합니다.', '#dc3545');
                validationStatus.password = false;
            }
            updateSubmitButtonState();
        });
    }

    if (userIdInput && userIdHelper) {
        userIdInput.addEventListener('blur', async (e) => {
            const userId = e.target.value.trim();
            if (userId.length < 4) {
                setHelperText(userIdHelper, '아이디는 4자 이상이어야 합니다.', '#dc3545');
                validationStatus.userId = false;
                updateSubmitButtonState();
                return;
            }
            try {
                const response = await fetch(`/api/members/check-userid?userId=${encodeURIComponent(userId)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(userIdHelper, '✓ 사용 가능한 아이디입니다.', '#1DB954');
                    validationStatus.userId = true;
                } else {
                    setHelperText(userIdHelper, '✗ 이미 사용 중인 아이디입니다.', '#dc3545');
                    validationStatus.userId = false;
                }
            } catch (error) {
                setHelperText(userIdHelper, '중복 확인 중 오류가 발생했습니다.', '#dc3545');
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
                setHelperText(emailHelper, '유효한 이메일 형식이 아닙니다.', '#dc3545');
                validationStatus.email = false;
                updateSubmitButtonState();
                return;
            }
            try {
                const response = await fetch(`/api/members/check-email?email=${encodeURIComponent(email)}`);
                const isAvailable = await response.json();
                if (isAvailable) {
                    setHelperText(emailHelper, '✓ 사용 가능한 이메일입니다.', '#1DB954');
                    validationStatus.email = true;
                } else {
                    setHelperText(emailHelper, '✗ 이미 사용 중인 이메일입니다.', '#dc3545');
                    validationStatus.email = false;
                }
            } catch (error) {
                setHelperText(emailHelper, '중복 확인 중 오류가 발생했습니다.', '#dc3545');
                validationStatus.email = false;
            }
            updateSubmitButtonState();
        });
    }

    signupForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (submitButton.disabled) {
            alert('입력 정보를 다시 확인해주세요. (예: 아이디 또는 이메일 중복)');
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
                    alert(`입력 오류: ${firstErrorMessage}`);
                } else {
                    alert('회원가입에 실패했습니다.');
                }
                throw new Error('Signup failed');
            }

            alert('회원가입 성공!');
            // 회원가입 성공 후, 로그인 모달을 띄워줌
            signupForm.reset();
            // 헬퍼 텍스트들도 초기화 (선택 사항이지만 깔끔함)
            setHelperText(document.getElementById('userId-helper'), '', '#888');
            setHelperText(document.getElementById('email-helper'), '', '#888');
            setHelperText(document.getElementById('password-helper'), '6자 이상, 소문자와 숫자 필수', '#888');

            // 3. 회원가입 모달은 닫고
            signupModal.classList.remove('active');

            // 4. 로그인 모달을 바로 띄워줍니다.
            loginModal.classList.add('active');

        } catch (error) {
            console.error("Signup submit failed:", error);
        }
    });

    updateSubmitButtonState(); // 페이지 로드 시 버튼 비활성화
}