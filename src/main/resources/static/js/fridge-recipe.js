document.addEventListener("DOMContentLoaded", () => {
  loadRecipe();

  document.getElementById("newRecipeBtn").addEventListener("click", loadRecipe);

  document.getElementById("backBtn").addEventListener("click", () => {
    window.location.href = "/fridge"; // 이후에 냉장고 URL 넣기
  });
});

// --- 현재는 더미 AI 레시피 ---
function loadRecipe() {



  document.getElementById("recipe-title").textContent = recipe.title;
  document.getElementById("recipe-meta").textContent = recipe.meta;

  const ingList = document.getElementById("ingredient-list");
  ingList.innerHTML = recipe.ingredients.map(i => `<li>${i}</li>`).join("");

  const seasoning = document.getElementById("seasoning-list");
  seasoning.innerHTML = recipe.seasoning.map(s => `<li>${s}</li>`).join("");

  const steps = document.getElementById("steps-list");
  steps.innerHTML = recipe.steps.map(s => `<li>${s}</li>`).join("");
}
