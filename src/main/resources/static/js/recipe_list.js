document.addEventListener("DOMContentLoaded", () => {
    
    // ============================================
    // 1. 필터 버튼 이벤트 (페이지 리로드)
    // ============================================
    const urlParams = new URLSearchParams(window.location.search);

    // [종류별 필터]
    document.querySelectorAll("#categoryType .filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.getAttribute("data-type-id");
            
            // 1. 카테고리 ID 설정
            urlParams.set("typeCatId", id);
            
            // 2. ★ 중요: 카테고리 선택 시 기존 '검색어(keyword)'는 삭제 ★
            urlParams.delete("keyword"); 
            
            // 3. 페이지 1로 초기화
            urlParams.set("page", 1); 
            
            window.location.href = "/recipe/list?" + urlParams.toString();
        });
    });

    // [방법별 필터]
    document.querySelectorAll("#categoryMethod .filter-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.getAttribute("data-method-id");
            
            // 1. 카테고리 ID 설정
            urlParams.set("methodCatId", id);
            
            // 2. ★ 중요: 카테고리 선택 시 기존 '검색어(keyword)'는 삭제 ★
            urlParams.delete("keyword");
            
            // 3. 페이지 1로 초기화
            urlParams.set("page", 1);
            
            window.location.href = "/recipe/list?" + urlParams.toString();
        });
    });

    // ============================================
    // 2. 카드 클릭 시 상세 모달 열기 (AJAX)
    // ============================================
    const cards = document.querySelectorAll(".recipe-card");
    cards.forEach(card => {
        card.addEventListener("click", () => {
            const recipeId = card.getAttribute("data-id");
            if(recipeId) {
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
        .then(res => res.json())
        .then(data => {
            if(data.status === "OK") {
                renderDetailView(data.recipe, data.isFavorite);
            } else {
                alert("정보를 불러오지 못했습니다.");
            }
        })
        .catch(err => {
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
    
    // 1. 기본 정보
    setText("detailTitle", recipe.name); 
    setText("detailCookTime", recipe.time + "분");     
    setText("detailServing", recipe.serving + "인분");   

    // 2. 재료 & 양념
    renderList("detailIngredients", recipe.ingredient);
    renderList("detailSeasoning", recipe.spicyIngredient || "양념 정보 없음");

    // 3. 조리 순서
    renderList("detailCooking", recipe.methodSteps);

    // 4. 즐겨찾기 상태
    updateFavoriteUI(isFavorite);
}

// 텍스트 바인딩 헬퍼
function setText(id, text) {
    const el = document.getElementById(id);
    if(el) el.textContent = text || "-";
}

// 리스트 생성 헬퍼
function renderList(elementId, data) {
    const el = document.getElementById(elementId);
    if(!el) return;

    let listArray = [];

    if (!data) {
        el.innerHTML = "<li>정보 없음</li>";
        return;
    }

    if (Array.isArray(data)) {
        listArray = data;
    } else if (typeof data === 'string') {
        if(data.includes('\n')) {
            listArray = data.split('\n');
        } else {
            listArray = data.split(',');
        }
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
        body: `recipeId=${recipeId}`
    })
    .then(res => res.json())
    .then(data => {
        if (data.status === "UNAUTHORIZED") {
            if(confirm(data.message)) {
                window.location.href = data.redirectUrl;
            }
        } else if (data.status === "OK") {
            updateFavoriteUI(data.isFavorite);
        } else {
            alert(data.message);
        }

        if (data.isFavorite) {
                showAutoModal("즐겨찾기에 추가되었습니다");
            } else {
                showAutoModal("즐겨찾기에서 삭제되었습니다");
            }
    })
    .catch(err => console.error(err));
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