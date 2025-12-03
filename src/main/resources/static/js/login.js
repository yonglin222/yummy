document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");

    // 폼이 존재할 때만 이벤트 리스너 등록
    if (loginForm) {
        loginForm.addEventListener("submit", function(e) {
            const email = document.getElementById("email");
            const password = document.getElementById("password");
            
            const jsMsg = document.getElementById("jsMessage");
            const serverMsg = document.getElementById("loginMessage");

            // 1. 기존 서버 메시지가 떠 있다면 일단 숨김
            if (serverMsg) {
                serverMsg.style.display = "none";
            }
            
            // 2. 입력값 검사 (비어있는지 확인)
            if (!email.value.trim() || !password.value.trim()) {
                // (1) 전송 막기 (서버로 안 보냄)
                e.preventDefault(); 
                
                // (2) 에러 메시지 띄우기
                if (jsMsg) {
                    jsMsg.style.display = "block";
                    jsMsg.textContent = "이메일과 비밀번호를 입력해주세요.";
                    jsMsg.style.color = "#d42e2e";
                }
            } 
            // 3. 입력값이 다 있으면? -> 아무것도 안 함 (브라우저가 알아서 서버로 전송)
        });
    }
});