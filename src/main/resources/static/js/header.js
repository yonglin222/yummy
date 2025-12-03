// 회원 전용 메뉴 클릭 시 확인
document.addEventListener("DOMContentLoaded", () => {
    
  const loginField = document.getElementById("isLoggedInField");
  const isLoggedIn = (loginField && loginField.value === "true");
  const memberOnlyLinks = document.querySelectorAll(".member-only");

  memberOnlyLinks.forEach(link => {
    link.addEventListener("click", (e) => {
      // 로그인이 안 되어 있다면
      if (!isLoggedIn) {
        e.preventDefault(); // 링크 이동 막기
        
        // 1. 모달 띄우기
        if (typeof showModal === "function") {
            showModal("로그인 후 이용할 수 있습니다.");

            // 2. 모달의 '닫기(확인)' 버튼 찾기
            const closeBtn = document.getElementById("closeModalBtn");

            // 3. 버튼에 '클릭 시 로그인 페이지 이동' 이벤트 추가
            if (closeBtn) {
                // 기존에 다른 이벤트가 겹치지 않도록 { once: true } 옵션 사용 (한 번 실행 후 삭제됨)
                closeBtn.addEventListener("click", function() {
                    location.href = "/user/loginForm"; 
                }, { once: true });
            }

        } else {
            // 모달 JS가 없을 경우 백업
            alert("로그인 후 이용할 수 있습니다");
            location.href = "/user/loginForm";
        }
      }
    });
  });
});