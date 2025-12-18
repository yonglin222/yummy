/**
 * fridge-recipe.js
 * 역할:
 * - URL 파라미터로 recipeId, aiMessage 수신
 * - Spring API (/recipe/api/detail/{id}) 로 레시피 상세 조회
 * - 화면 렌더링
 * - "같은 재료로 다시 추천" → 냉장고 페이지로 이동
 * - "나의 냉장고로 돌아가기" 처리
 */

/* =========================================
   전역 변수
   ========================================= */

// (참고용) 이전 AI 메시지
let currentAiMessage = "";


/* =========================================
   DOM 로드
   ========================================= */

document.addEventListener("DOMContentLoaded", () => {
    const urlParams = new URLSearchParams(window.location.search);

    const recipeId = urlParams.get("recipeId");
    const aiMessage = urlParams.get("aiMessage");

    if (aiMessage) {
        currentAiMessage = decodeURIComponent(aiMessage);
    }

    // 1️⃣ 레시피 ID 유효성 체크
    if (!recipeId) {
        showError(
            "레시피를 찾을 수 없습니다.",
            "유효한 레시피 ID를 전달받지 못했습니다."
        );
        return;
    }

    // 2️⃣ AI 메시지 표시
    document.getElementById("ai-response-message").textContent =
        currentAiMessage || "AI가 냉장고 재료를 기반으로 레시피를 추천했어요!";

    // 3️⃣ 레시피 상세 로드
    loadRecipeDetail(recipeId);

    // 4️⃣ 버튼 이벤트
    bindButtons();
});


/* =========================================
   버튼 이벤트
   ========================================= */

function bindButtons() {

    // 같은 재료로 다시 추천
    document.getElementById("newRecipeBtn").addEventListener("click", () => {
        // 👉 냉장고 페이지로 돌아가서 다시 선택/추천
        window.location.href = "/fridge/list";
    });

    // 냉장고로 돌아가기
    document.getElementById("backToFridgeBtn").addEventListener("click", () => {
        window.location.href = "/fridge/list";
    });
}


/* =========================================
   레시피 상세 로드
   ========================================= */

function loadRecipeDetail(recipeId) {

    fetch(`/recipe/api/detail/${recipeId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.status !== "OK" || !data.recipe) {
                throw new Error("Invalid recipe response");
            }

            renderRecipe(data.recipe);
        })
        .catch(err => {
            console.error("레시피 상세 조회 오류:", err);
            showError(
                "레시피 정보를 불러오지 못했습니다.",
                "잠시 후 다시 시도해주세요."
            );
        });
}


/* =========================================
   렌더링
   ========================================= */

function renderRecipe(recipe) {

    // 제목
    document.getElementById("recipe-title").textContent = recipe.name;

    // 메타 정보
    document.getElementById("recipe-cookTime").textContent =
        `${recipe.time ?? 0}분`;
    document.getElementById("recipe-serving").textContent =
        `${recipe.serving ?? 0}인분`;

    // 재료
    renderList(
        "ingredient-list",
        recipe.ingredient
    );

    // 양념
    renderList(
        "seasoning-list",
        recipe.spicyIngredient
    );

    // 만드는 법
    renderSteps(
        "steps-list",
        recipe.methodSteps
    );
}


function renderList(elementId, text) {
    const el = document.getElementById(elementId);
    const safeText = text || "";

    el.innerHTML = safeText
        .split(/[\n,]/)
        .map(v => v.trim())
        .filter(v => v.length > 0)
        .map(v => `<li>${v}</li>`)
        .join("");
}


function renderSteps(elementId, steps) {
    const el = document.getElementById(elementId);

    if (!Array.isArray(steps) || steps.length === 0) {
        el.innerHTML = "<li>조리 단계 정보가 없습니다.</li>";
        return;
    }

    el.innerHTML = steps
        .map(step => `<li>${step}</li>`)
        .join("");
}


/* =========================================
   에러 처리
   ========================================= */

function showError(title, message) {
    document.getElementById("recipe-title").textContent = title;
    document.getElementById("ai-response-message").textContent = message;
}