document.addEventListener("DOMContentLoaded", () => {
    
    // 1. 로그인 여부 체크
    const loginField = document.getElementById("isLoggedInField");
    const isLoggedIn = (loginField && loginField.value === "true");
    const memberOnlyLinks = document.querySelectorAll(".member-only");

    memberOnlyLinks.forEach(link => {
        // [중요] async 키워드 추가 (showConfirmModal을 기다리기 위해)
        link.addEventListener("click", async (e) => {
            
            // 로그인이 안 되어 있다면
            if (!isLoggedIn) {
                e.preventDefault(); // 페이지 이동 막기
                
                // 프론트팀이 만든 showConfirmModal 함수 사용
                if (typeof showConfirmModal === "function") {
                    
                    // 2. 모달을 띄우고 사용자의 선택을 기다림 (await)
                    // "확인"을 누르면 result가 true, "취소"를 누르면 false가 됨
                    const result = await showConfirmModal("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?");
                    
                    // 3. 확인 버튼을 눌렀을 때만 이동
                    if (result) {
                        location.href = "/user/loginForm";
                    }

                } else {
                    // 비상용 (모달 JS가 없을 때)
                    if(confirm("로그인이 필요합니다. 이동하시겠습니까?")) {
                        location.href = "/user/loginForm";
                    }
                }
            }
        });
    });
});