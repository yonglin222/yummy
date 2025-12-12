document.addEventListener("DOMContentLoaded", () => {
    // ========== DOM ELEMENTS ==========
    const nicknameInput = document.getElementById("nickname");
    const emailInput = document.getElementById("email");
    const pwInput = document.getElementById("password");
    const pwCheckInput = document.getElementById("passwordCheck");

    const nicknameMsg = document.getElementById("nicknameMsg");
    const emailMsg = document.getElementById("emailMsg");
    const checkMsg = document.getElementById("checkMsg");
    const pwMsg = document.getElementById("pwMsg");

    const nicknameCheckBtn = document.querySelector(".nickname-check");
    const emailCheckBtn = document.querySelector(".email-check");
    
    const registForm = document.getElementById("registForm");

    // 상태 변수
    let isNickChecked = false;
    let isEmailChecked = false;

    // 입력값 변경 시 중복확인 초기화
    nicknameInput.addEventListener("input", () => { isNickChecked = false; nicknameMsg.textContent = ""; });
    emailInput.addEventListener("input", () => { isEmailChecked = false; emailMsg.textContent = ""; });

    // ========== 1. 닉네임 중복확인 (AJAX) ==========
    nicknameCheckBtn.addEventListener("click", () => {
        const nickname = nicknameInput.value.trim();
        nicknameMsg.textContent = "";

        if (!nickname) {
            nicknameMsg.textContent = "닉네임을 입력해주세요.";
            nicknameMsg.style.color = "#d42e2e";
            return;
        }

        const nicknameRegex = /^[A-Za-z가-힣]{2,10}$/; 
        if (!nicknameRegex.test(nickname)) {
            nicknameMsg.textContent = "닉네임은 2~10자의 한글, 영문만 가능합니다.";
            nicknameMsg.style.color = "#d42e2e";
            return;
        }

        $.ajax({
            type: "POST",
            url: "/api/check-nickname",
            data: { nickname: nickname },
            success: function(response) {
                if (response.available) {
                    nicknameMsg.textContent = "사용 가능한 닉네임입니다.";
                    nicknameMsg.style.color = "#3ca63c";
                    isNickChecked = true;
                } else {
                    nicknameMsg.textContent = "이미 사용 중인 닉네임입니다.";
                    nicknameMsg.style.color = "#d42e2e";
                    isNickChecked = false;
                }
            },
            error: function() {
                // 알림창 대신 텍스트로 표시하거나 모달 사용 가능
                nicknameMsg.textContent = "중복 확인 중 오류가 발생했습니다.";
                nicknameMsg.style.color = "#d42e2e";
            }
        });
    });

    // ========== 2. 이메일 중복확인 (AJAX) ==========
    emailCheckBtn.addEventListener("click", () => {
        const email = emailInput.value.trim();
        emailMsg.textContent = "";

        if (!email) {
            emailMsg.textContent = "이메일을 입력해주세요.";
            emailMsg.style.color = "#d42e2e";
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            emailMsg.textContent = "올바른 이메일 형식이 아닙니다.";
            emailMsg.style.color = "#d42e2e";
            return;
        }

        $.ajax({
            type: "POST",
            url: "/api/check-email",
            data: { email: email },
            success: function(isDuplicate) {
                if (!isDuplicate) { 
                    emailMsg.textContent = "사용 가능한 이메일입니다.";
                    emailMsg.style.color = "#3ca63c";
                    isEmailChecked = true;
                } else {
                    emailMsg.textContent = "이미 사용 중인 이메일입니다.";
                    emailMsg.style.color = "#d42e2e";
                    isEmailChecked = false;
                }
            },
            error: function() {
                emailMsg.textContent = "중복 확인 중 오류가 발생했습니다.";
                emailMsg.style.color = "#d42e2e";
            }
        });
    });

    // ========== 3. 비밀번호 유효성 검사 ==========
    pwInput.addEventListener("input", validatePassword);
    pwCheckInput.addEventListener("input", validatePassword);

    function validatePassword() {
        const pw = pwInput.value.trim();
        const pwc = pwCheckInput.value.trim();
        const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;

        if (!pwRegex.test(pw)) {
            checkMsg.textContent = "비밀번호는 8자~16자(영어 소문자,특수문자,숫자 각1개 이상)로 작성해주세요.";
            checkMsg.style.color = "#d42e2e";
        } else {
            checkMsg.textContent = "사용 가능한 비밀번호입니다.";
            checkMsg.style.color = "#3ca63c";
        }

        if (pwc) {
            if (pw !== pwc) {
                pwMsg.textContent = "비밀번호가 일치하지 않습니다.";
                pwMsg.style.color = "#d42e2e";
            } else {
                pwMsg.textContent = "비밀번호가 일치합니다.";
                pwMsg.style.color = "#3ca63c";
            }
        } else {
            pwMsg.textContent = "";
        }
    }

      // ========== 4. 회원가입 제출 (Submit) ==========
  registForm.addEventListener("submit", (e) => {
    // 일단 제출을 막고 유효성 검사 진행
    e.preventDefault();

    const pw = pwInput.value.trim();
    const pwc = pwCheckInput.value.trim();
    const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;

    // 유효성 검사
    if (!isNickChecked) {
      if (typeof showAutoModal === "function")
        showAutoModal("닉네임 중복확인을 해주세요.");
      return;
    }

    if (!isEmailChecked) {
      if (typeof showAutoModal === "function")
        showAutoModal("이메일 중복확인을 해주세요.");
      else alert("이메일 중복확인을 해주세요.");
      return;
    }

    if (!pwRegex.test(pw)) {
      if (typeof showAutoModal === "function")
        showAutoModal("비밀번호 형식을 확인해주세요.");
      return;
    }

    if (pw !== pwc) {
      if (typeof showAutoModal === "function")
        showAutoModal("비밀번호가 일치하지 않습니다.");
      return;
    }

    // 모든 검사 통과 시 실제 폼 제출
    if (typeof showAutoModal === "function") {
      showAutoModal("회원가입이 완료되었습니다.");
      
      setTimeout(() => {
        registForm.submit();
      }, 1200);
    }
  });
});
