document.addEventListener("DOMContentLoaded", () => {
    
    // 1. 카드 클릭 이벤트 (상세 모달 열기)
    const cards = document.querySelectorAll(".recipe-card");
    cards.forEach(card => {
        card.addEventListener("click", () => {
            const recipeId = card.getAttribute("data-id");
            if (recipeId) {
                loadRecipeDetail(recipeId);
            }
        });
    });

    // 2. 뒤로가기 버튼
    const backBtn = document.getElementById("backToList");
    if (backBtn) {
        backBtn.addEventListener("click", () => {
            document.getElementById("recipeDetailPage").style.display = "none";
            document.getElementById("recipeListPage").style.display = "block";
        });
    }

    // 3. 즐겨찾기 버튼 (상세화면 내부)
    const favBtn = document.getElementById("favoriteBtn");
    if (favBtn) {
        favBtn.onclick = () => {
            if (!currentRecipeId) return;
            toggleFavorite(currentRecipeId);
        };
    }
});

let currentRecipeId = null;

// ============================================
// API: 상세 정보 가져오기
// ============================================
function loadRecipeDetail(id) {
    fetch(`/recipe/api/detail/${id}`)
        .then(res => res.json())
        .then(data => {
            if (data.status === "OK") {
                renderDetailView(data.recipe, data.isFavorite);
            } else {
                alert("레시피 정보를 불러오지 못했습니다.");
            }
        })
        .catch(err => console.error(err));
}

// ============================================
// UI: 상세 화면 그리기
// ============================================
function renderDetailView(recipe, isFavorite) {
    currentRecipeId = recipe.recipeId;

    // 화면 전환
    document.getElementById("recipeListPage").style.display = "none";
    document.getElementById("recipeDetailPage").style.display = "block";

    // 텍스트 바인딩
    setText("detailTitle", recipe.name);
    setText("detailCookTime", recipe.time + "분");
    setText("detailServing", recipe.serving + "인분");

    // 리스트 바인딩
    renderList("detailIngredients", recipe.ingredient);
    renderList("detailSeasoning", recipe.spicyIngredient || "양념 정보 없음");
    renderList("detailCooking", recipe.methodSteps);

    // 즐겨찾기 버튼 UI 업데이트
    updateFavoriteUI(isFavorite);
}

// 텍스트 설정 헬퍼
function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text || "-";
}

// 리스트 생성 헬퍼
function renderList(elementId, data) {
    const el = document.getElementById(elementId);
    if (!el) return;

    let listArray = [];
    if (!data) {
        el.innerHTML = "<li>정보 없음</li>";
        return;
    }

    if (Array.isArray(data)) {
        listArray = data;
    } else if (typeof data === 'string') {
        if (data.includes('\n')) listArray = data.split('\n');
        else listArray = data.split(',');
    }

    if (listArray.length === 0) {
        el.innerHTML = "<li>정보 없음</li>";
    } else {
        el.innerHTML = listArray
            .map(item => `<li>${item.trim()}</li>`)
            .join("");
    }
}

// ============================================
// API: 즐겨찾기 토글
// ============================================
function toggleFavorite(recipeId) {
  fetch("/recipe/toggleFavorite", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `recipeId=${recipeId}`,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.status === "UNAUTHORIZED") {
        if (confirm(data.message)) {
          window.location.href = data.redirectUrl;
        }
      } else if (data.status === "OK") {
        updateFavoriteUI(data.isFavorite);

        // (선택사항) 즐겨찾기 목록 페이지이므로, 삭제 시 새로고침을 할지 물어보거나 UI 갱신
        if (!data.isFavorite) {
          showAutoModal("즐겨찾기에서 삭제되었습니다.");

          setTimeout(() => {
            location.reload();
          }, 1200);
        }
      } else {
        alert(data.message);
      }
    })
    .catch((err) => console.error(err));
}

function updateFavoriteUI(isActive) {
    const favBtn = document.getElementById("favoriteBtn");
    const textSpan = favBtn.querySelector(".text");
    const icon = favBtn.querySelector("i");

    if (isActive) {
        // [즐겨찾기 된 상태]
        favBtn.classList.add("active");
        textSpan.textContent = "즐겨찾기 추가됨";
        icon.className = "fa-solid fa-star";
        
        // ★ 이 줄이 핵심입니다! (마우스 올렸을 때 '삭제하기'로 텍스트 변경)
        favBtn.dataset.hoverText = "즐겨찾기 삭제하기"; 
    } else {
        // [즐겨찾기 안 된 상태]
        favBtn.classList.remove("active");
        textSpan.textContent = "즐겨찾기";
        icon.className = "fa-regular fa-star";
        
        // ★ 마우스 올렸을 때 '추가하기'로 텍스트 변경
        favBtn.dataset.hoverText = "즐겨찾기 추가하기";
    }
}