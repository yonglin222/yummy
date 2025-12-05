function createPagination({
  containerId,
  totalItems,
  currentPage,
  pageSize,
  onPageChange
}) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const totalPages = Math.ceil(totalItems / pageSize);
  container.innerHTML = "";

  // 이전 버튼
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "〈";
  prevBtn.disabled = currentPage === 1;
  prevBtn.onclick = () => onPageChange(currentPage - 1);
  container.appendChild(prevBtn);

  // 페이지 번호 버튼
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPage) btn.classList.add("active");
    btn.onclick = () => onPageChange(i);
    container.appendChild(btn);
  }

  // 다음 버튼
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "〉";
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.onclick = () => onPageChange(currentPage + 1);
  container.appendChild(nextBtn);
}