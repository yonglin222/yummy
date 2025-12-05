document.addEventListener("DOMContentLoaded", () => {
  // ========== INPUT ELEMENTS ==========
  const nicknameInput = document.getElementById("nickname");
  const emailInput = document.getElementById("email");
  const pwInput = document.getElementById("password");
  const pwCheckInput = document.getElementById("passwordCheck");

  // ========== MESSAGE ELEMENTS ==========
  const nicknameMsg = document.getElementById("nicknameMsg");
  const emailMsg = document.getElementById("emailMsg");
  const checkMsg = document.getElementById("checkMsg");
  const pwMsg = document.getElementById("pwMsg");

  // ========== BUTTONS ==========
  const nicknameCheckBtn = document.querySelector(".nickname-check");
  const emailCheckBtn = document.querySelector(".email-check");
  const signupBtn = document.querySelector(".register-btn");

  // ========== MOCK DATA (UI 단계) ==========
  const mockNicknames = ["apple", "banana", "hello", "강민"];
  const mockEmails = ["test@test.com", "user@gmail.com", "hello@naver.com"];

  let isNickChecked = false;
  let isEmailChecked = false;

  // 닉네임 중복확인
  nicknameCheckBtn.addEventListener("click", () => {
    const nickname = nicknameInput.value.trim();
    nicknameMsg.textContent = "";

    if (!nickname) {
      nicknameMsg.textContent = "닉네임을 입력해주세요.";
      return;
    }

    const nicknameRegex = /^[A-Za-z가-힣]+$/;

    if (!nicknameRegex.test(nickname)) {
      nicknameMsg.textContent = "닉네임은 한글 또는 영문만 사용할 수 있습니다.";
      return;
    }

    if (nickname.length > 10) {
      nicknameMsg.textContent = "닉네임은 10자 이하로 작성해주세요.";
      return;
    }

    if (nickname)
      if (mockNicknames.includes(nickname)) {
        nicknameMsg.textContent = "이미 사용 중인 닉네임입니다.";
        return;
      }

    isNickChecked = true;
    nicknameMsg.textContent = "사용 가능한 닉네임입니다.";
    nicknameMsg.style.color = "#3ca63c";
  });

  // ◆ 이메일 중복확인
  emailCheckBtn.addEventListener("click", () => {
    const email = emailInput.value.trim();
    emailMsg.textContent = "";

    if (!email) {
      emailMsg.textContent = "이메일을 입력해주세요.";
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      emailMsg.textContent = "올바른 이메일 형식이 아닙니다.";
      return;
    }

    if (mockEmails.includes(email)) {
      emailMsg.textContent = "이미 사용 중인 이메일입니다.";
      return;
    }

    isEmailChecked = true;
    emailMsg.textContent = "사용 가능한 이메일입니다.";
    emailMsg.style.color = "#3ca63c";
  });

  // ◆ 비밀번호 확인 (일치 여부)
  pwCheckInput.addEventListener("input", () => {
    const pw = pwInput.value.trim();
    const pwc = pwCheckInput.value.trim();

    const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;

    if (!pwRegex.test(pw)) {
      checkMsg.textContent = "비밀번호는 8자~16자(영어 소문자,특수문자,숫자 각1개 이상)포함 작성해주세요.";
    } else {
      checkMsg.textContent = "";
    }

    if (!pwc) {
      pwMsg.textContent = "";
      return;
    }

    if (pw !== pwc) {
      pwMsg.textContent = "비밀번호가 일치하지 않습니다.";
      pwMsg.style.color = "#d42e2e"
    } else {
      pwMsg.textContent = "비밀번호가 일치합니다.";
      pwMsg.style.color = "#3ca63c";
    }
  });

  // ◆ 가입하기 버튼 (전체 유효성 검사)
  signupBtn.addEventListener("click", () => {
    const nickname = nicknameInput.value.trim();
    const email = emailInput.value.trim();
    const pw = pwInput.value.trim();
    const pwc = pwCheckInput.value.trim();

    // 닉네임 검사
    if (!isNickChecked) {
      showModal("닉네임 중복확인을 해주세요.")
      return;
    }

    // 이메일 검사
    if (!isEmailChecked) {
      showModal("이메일 중복확인을 해주세요.")
      return;
    }

    // 비밀번호 검사 (정규식 포함)
    const pwRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,16}$/;
    if (!pwRegex.test(pw)) {
      return;
    }

    // 비밀번호 일치 검사
    if (pw !== pwc) {
      return;
    }

    showModal("회원가입이 완료되었습니다", () => {
      window.location.href = "login.html";
    });
  });
});
