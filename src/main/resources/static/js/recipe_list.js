document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".recipe-thumbnail").forEach((img) => {
    img.addEventListener("click", (e) => {
      e.stopPropagation(); // 🔥 카드 클릭 이벤트 차단
    });
  });
  // ============================================
  // 1. 필터 버튼 이벤트 (페이지 리로드)
  // ============================================
  const urlParams = new URLSearchParams(window.location.search);

  // 종류별 필터
  document.querySelectorAll("#categoryType .filter-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-type-id");
      urlParams.set("typeCatId", id);
      urlParams.set("page", 1); // 필터 변경 시 1페이지로
      window.location.href = "/recipe/list?" + urlParams.toString();
    });
  });

  // 방법별 필터
  document.querySelectorAll("#categoryMethod .filter-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-method-id");
      urlParams.set("methodCatId", id);
      urlParams.set("page", 1);
      window.location.href = "/recipe/list?" + urlParams.toString();
    });
  });

  // ============================================
  // 2. 카드 클릭 시 상세 모달 열기 (AJAX)
  // ============================================
  const cards = document.querySelectorAll(".recipe-card");
  cards.forEach((card) => {
    card.addEventListener("click", () => {
      const recipeId = card.getAttribute("data-id");
      if (recipeId) {
        loadRecipeDetail(recipeId);
      }
    });
  });

  // ============================================
  // 3. 뒤로가기 & 즐겨찾기 버튼 (상세화면 내부)
  // ============================================
  const backBtn = document.getElementById("backToList");
  if (backBtn) {
    backBtn.addEventListener("click", () => {
      document.getElementById("recipeDetailPage").style.display = "none";
      document.getElementById("recipeListPage").style.display = "block";
    });
  }

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
    .then((res) => res.json())
    .then((data) => {
      if (data.status === "OK") {
        renderDetailView(data.recipe, data.isFavorite);
      } else {
        alert("정보를 불러오지 못했습니다.");
      }
    })
    .catch((err) => {
      console.error(err);
      alert("서버 통신 오류");
    });
}

// ============================================
// UI: 상세 화면 렌더링
// ============================================
function renderDetailView(recipe, isFavorite) {
  currentRecipeId = recipe.recipeId;

  // 화면 전환
  document.getElementById("recipeListPage").style.display = "none";
  document.getElementById("recipeDetailPage").style.display = "block";

  const img = document.getElementById("detailImage");
  img.src = `/img/recipe/recipe_${recipe.recipeId}.jpg`;
  img.onerror = () => {
    img.src = "/img/recipe/default.jpg";
  };

  // 1. 기본 정보 (DTO 필드명: name, time, serving)
  setText("detailTitle", recipe.name);
  setText("detailCookTime", recipe.time + "분");
  setText("detailServing", recipe.serving + "인분");

  // 2. 재료 & 양념 (String -> List 변환)
  renderList("detailIngredients", recipe.ingredient);
  renderList("detailSeasoning", recipe.spicyIngredient || "양념 정보 없음");

  // 3. 조리 순서 (List -> List)
  renderList("detailCooking", recipe.methodSteps);

  // 4. 즐겨찾기 상태
  updateFavoriteUI(isFavorite);
}

// 텍스트 바인딩 헬퍼
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

  // 배열이면 그대로 사용, 문자열이면 콤마/엔터로 분리
  if (Array.isArray(data)) {
    listArray = data;
  } else if (typeof data === "string") {
    if (data.includes("\n")) {
      listArray = data.split("\n");
    } else {
      listArray = data.split(",");
    }
  }

  if (listArray.length === 0) {
    el.innerHTML = "<li>정보 없음</li>";
  } else {
    el.innerHTML = listArray.map((item) => `<li>${item.trim()}</li>`).join("");
  }
}

// ============================================
// API: 즐겨찾기 토글
// ============================================
// [중요] async 키워드를 추가하여 비동기 모달 응답을 기다릴 수 있게 합니다.
async function toggleFavorite(recipeId) {
  fetch("/recipe/toggleFavorite", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `recipeId=${recipeId}`,
  })
    .then((res) => res.json())
    .then(async (data) => { // 데이터 처리 부분에도 async 추가
      
      // 1. 로그인 안 된 상태 (UNAUTHORIZED) 처리
      if (data.status === "UNAUTHORIZED") {
        
        if (typeof showConfirmModal === "function") {
          // 프론트팀이 만든 커스텀 확인 모달 띄우기
          const result = await showConfirmModal("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?");
          
          // 사용자가 '확인'을 눌렀을 때만 로그인 페이지로 이동
          if (result) {
            location.href = "/user/loginForm";
          }
        } else {
          // 커스텀 모달 함수가 없을 경우 대비 (비상용 브라우저 기본 confirm)
          if (confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            location.href = "/user/loginForm";
          }
        }
        return; // 로그인 체크 후 함수 종료
      }

      // 2. 정상적으로 즐겨찾기 토글이 완료된 경우 (OK)
      if (data.status === "OK") {
        updateFavoriteUI(data.isFavorite);

        if (data.isFavorite) {
          showAutoModal("즐겨찾기에 추가되었습니다");
        } else {
          showAutoModal("즐겨찾기에서 삭제되었습니다");
        }
        return;
      }
    })
    .catch((err) => {
      console.error(err);
      alert("서버 통신 중 오류가 발생했습니다.");
    });
}

function updateFavoriteUI(isActive) {
  const favBtn = document.getElementById("favoriteBtn");
  const textSpan = favBtn.querySelector(".text");
  const icon = favBtn.querySelector("i");

  if (isActive) {
    favBtn.classList.add("active");
    textSpan.textContent = "즐겨찾기 추가됨";
    icon.className = "fa-solid fa-star";
    favBtn.dataset.hoverText = "즐겨찾기 삭제하기";
  } else {
    favBtn.classList.remove("active");
    textSpan.textContent = "즐겨찾기";
    icon.className = "fa-regular fa-star";
    favBtn.dataset.hoverText = "즐겨찾기 추가하기";
  }
}
document.addEventListener("DOMContentLoaded", () => {
  // --- 기존 코드 (필터, 카드 클릭 이벤트 등) ---
  
  // 1. URL 파라미터 확인 로직 추가
  const urlParams = new URLSearchParams(window.location.search);
  const targetId = urlParams.get("targetId");

  // 만약 targetId 파라미터가 있다면 즉시 상세 정보 로드
  if (targetId) {
    loadRecipeDetail(targetId);
    
    // (선택사항) 주소창에서 targetId를 제거하고 싶다면 아래 주석 해제
    // history.replaceState({}, null, window.location.pathname);
  }

  // --- 아래는 기존에 제공해주신 코드들 ---
  document.querySelectorAll(".recipe-thumbnail").forEach((img) => {
    img.addEventListener("click", (e) => {
      e.stopPropagation();
    });
  });

  // 종류별 필터... 방법별 필터... 생략

  const cards = document.querySelectorAll(".recipe-card");
  cards.forEach((card) => {
    card.addEventListener("click", () => {
      const recipeId = card.getAttribute("data-id");
      if (recipeId) {
        loadRecipeDetail(recipeId);
      }
    });
  });

  // 뒤로가기 & 즐겨찾기 버튼 설정... 생략
});

// loadRecipeDetail, renderDetailView 등 나머지 함수들은 기존 코드 유지