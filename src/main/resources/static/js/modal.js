document.addEventListener("DOMContentLoaded", () => {
  const modalBox = document.querySelector(".modal-box");
  if (modalBox) {
    modalBox.addEventListener("click", e => e.stopPropagation());
  }
});

// 자동 모달(즐겨찾기, 레시피 불러오기 등)
function showAutoModal(message, duration = 1200) {
  const modal = document.getElementById("modal");
  const msg = document.getElementById("modal-message");
  const btnGroup = document.querySelector(".modal-btn-group");

  msg.textContent = message;
  // 버튼 숨김
  if (btnGroup) btnGroup.style.display = "none";

  modal.style.display = "flex";

  setTimeout(() => {
    modal.style.display = "none";
  }, duration);
}



// 확인 / 취소 모달 (true / false 리턴)
function showConfirmModal(message) {
  const modal = document.getElementById("modal");
  const msg = document.getElementById("modal-message");
  const btnGroup = document.querySelector(".modal-btn-group");
  const cancelBtn = document.getElementById("modalCancelBtn");
  const confirmBtn = document.getElementById("modalConfirmBtn");

  msg.textContent = message;
  btnGroup.style.display = "flex";
  modal.style.display = "flex";


  return new Promise((resolve) => {

    function close(result) {
      modal.style.display = "none";
      cancelBtn.removeEventListener("click", onCancel);
      confirmBtn.removeEventListener("click", onConfirm);
      modal.removeEventListener("click", onOutsideClick);
      resolve(result);
    }

    function onCancel() {
      close(false);
    }

    function onConfirm() {
      close(true);
    }

    function onOutsideClick(e) {
      if (e.target === modal) close(false);
    }

    cancelBtn.addEventListener("click", onCancel);
    confirmBtn.addEventListener("click", onConfirm);
    modal.addEventListener("click", onOutsideClick);
  });
}