// 로그인 상태 (임시)
let isLoggedIn = false;

// 회원 전용 메뉴 클릭 시 확인
document.addEventListener("DOMContentLoaded", () => {
  const memberOnlyLinks = document.querySelectorAll(".member-only");

  memberOnlyLinks.forEach(link => {
    link.addEventListener("click", (e) => {
      if (!isLoggedIn) {
        e.preventDefault(); //링크 막기
        showConfirmModal("로그인 후 이용할 수 있습니다");
      }
    });
  });
});
