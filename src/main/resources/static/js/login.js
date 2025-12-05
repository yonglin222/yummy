document.addEventListener("DOMContentLoaded", () => {
  const email = document.getElementById("email");
  const password = document.getElementById("password");
  const loginBtn = document.querySelector(".logins-btn");
  const msg = document.getElementById("loginMsg");

  function tryLogin() {
    if (msg) msg.textContent = "";

    const userEmail = email.value.trim();
    const userPw = password.value.trim();

    if (!userEmail || !userPw) {
      msg.textContent = ("이메일과 비밀번호를 입력해주세요.");
      return;
    }

    // UI 테스트용 로그인
    if (userEmail === "test@test.com" && userPw === "1234") {
      window.location.href = "main.html";
    } else {
      msg.textContent = ("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
  }

  loginBtn.addEventListener("click", tryLogin);

  document.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      tryLogin();
    }
  });
});