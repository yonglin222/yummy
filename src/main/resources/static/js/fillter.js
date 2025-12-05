let recipesData = dummyRecipes; // ⭐ DB 연동 시 API 연결하면 됨
let filteredData = recipesData;

let selectedType = null;
let selectedMethod = null;
let searchKeyword = "";

function applyFilters() {
  filteredData = recipesData;

  if (searchKeyword) {
    filteredData = filteredData.filter((r) =>
      r.name.toLowerCase().includes(searchKeyword)
    );
  }

  if (selectedType) {
    filteredData = filteredData.filter((r) => r.type === selectedType);
  }

  if (selectedMethod) {
    filteredData = filteredData.filter((r) => r.method === selectedMethod);
  }

  document.getElementById("recipeDetailPage").style.display = "none";
  document.getElementById("recipeListPage").style.display = "block";

  currentPage = 1;
  renderPagedList();
}

document.addEventListener("DOMContentLoaded", () => {
  const typeButtons = document.querySelectorAll("#categoryType .filter-btn");
  const methodButtons = document.querySelectorAll(
    "#categoryMethod .filter-btn"
  );

  typeButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      searchKeyword = "";
      document.getElementById("searchInput").value = "";
      if (selectedType === btn.dataset.type) {
        selectedType = null;
        btn.classList.remove("active");
      } else {
        typeButtons.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        selectedType = btn.dataset.type;
      }
      applyFilters();
    });
  });

  methodButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      searchKeyword = "";
      document.getElementById("searchInput").value = "";
      if (selectedMethod === btn.dataset.type) {
        selectedMethod = null;
        btn.classList.remove("active");
      } else {
        methodButtons.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");
        selectedMethod = btn.dataset.type;
      }
      applyFilters();
    });
  });

  window.addEventListener("globalSearch", (e) => {
    searchKeyword = e.detail.keyword;

    selectedType = null;
    selectedMethod = null;
    document
      .querySelectorAll(".filter-btn")
      .forEach((b) => b.classList.remove("active"));

    applyFilters();
  });
});
