document.addEventListener("DOMContentLoaded", () => {

  let recipe = JSON.parse(localStorage.getItem("selectedRecipe"));

  // 제목 & 메타
  document.getElementById("recipe-title").textContent = recipe.title;
  document.getElementById("recipe-meta").textContent =
    `${recipe.servings} · ${recipe.time} · ${recipe.categoryText}`;

  // 재료
  const ingUl = document.getElementById("ingredient-list");
  recipe.ingredients.forEach(text => {
    const li = document.createElement("li");
    li.textContent = text;
    ingUl.appendChild(li);
  });

  // 양념재료
  const seasUl = document.getElementById("seasoning-list");
  recipe.seasonings.forEach(text => {
    const li = document.createElement("li");
    li.textContent = text;
    seasUl.appendChild(li);
  });

  // 만드는 법
  const stepsOl = document.getElementById("steps-list");
  recipe.steps.forEach(text => {
    const li = document.createElement("li");
    li.textContent = text;
    stepsOl.appendChild(li);
  });

    // 즐겨찾기 버튼
const favBtn = document.getElementById("favorite-btn");


favBtn.addEventListener("click", () => {
  const isActive = favBtn.classList.toggle("active");
  favBtn.textContent = isActive ? "★" : "☆";

  if (isActive) {
    showAutoModal("즐겨찾기에 추가되었습니다");
  } else {
    showAutoModal("즐겨찾기가 취소되었습니다");
  }
});

});
