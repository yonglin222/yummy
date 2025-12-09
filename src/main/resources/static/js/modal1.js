let modalOnClose = null;

function showModal(message, onClose) {
  const modal = document.getElementById("modal");
  const messageBox = document.getElementById("modal-message");
  const closeBtn = document.getElementById("closeModalBtn");

  if (!modal || !messageBox || !closeBtn) {
    console.error("모달 요소를 찾을 수 없습니다.");
    return;
  }

  modalOnClose = typeof onClose === "function" ? onClose : null;

  messageBox.textContent = message;
  modal.style.display = "flex";

  const handleClose = () => {
    closeModal();
  };

  closeBtn.onclick = handleClose;

  modal.onclick = (e) => {
    if (e.target === modal) {
      handleClose();
    }
  };

  function handleEnter(e) {
    if (e.key === "Enter") {
      document.removeEventListener("keydown", handleEnter);
      handleClose();
    }
  }
  document.addEventListener("keydown", handleEnter);
}

function closeModal() {
  const modal = document.getElementById("modal");
  if (!modal) return;

  modal.style.display = "none";


  if (typeof modalOnClose === "function") {
    const cb = modalOnClose;
    modalOnClose = null; 
    cb();
  }
}