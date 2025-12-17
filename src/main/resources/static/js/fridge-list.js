/* [카테고리 매핑]
  HTML/JS (영어) <-> DB (한글)
  - freezer: 냉동
  - fridge: 냉장
  - room: 상온
*/
const CATEGORY_MAP = {
    freezer: "냉동",
    fridge: "냉장",
    room: "상온"
};

const REVERSE_MAP = {
    "냉동": "freezer",
    "냉장": "fridge",
    "상온": "room"
};

// 현재 냉장고 데이터를 메모리에 유지
let currentIngredients = [];

document.addEventListener("DOMContentLoaded", () => {
    // 1. 초기 데이터 로드
    loadFridgeData();

    // 2. 재료 추가 버튼 이벤트 연결
    document.querySelectorAll(".add-ingredient-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const categoryKey = btn.dataset.category;
            addIngredient(categoryKey);
        });
    });

    // 3. 선택 삭제 버튼
    document.getElementById("deleteSelectedBtn")
        .addEventListener("click", handleDeleteSelected);

    // ⭐️ 4. 레시피 불러오기 버튼 이벤트 연결 (수정됨)
    document.getElementById('recipeSelectedBtn').addEventListener('click', () => {
        handleRecipeRecommendation('selected');
    });
    document.getElementById('recipeAllBtn').addEventListener('click', () => {
        handleRecipeRecommendation('all');
    });

    // 5. 인라인 수정 모드 (이벤트 위임)
    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("ingredient-name")) {
            enterNameEditMode(e.target);
        }
        if (e.target.classList.contains("ingredient-date")) {
            enterDateEditMode(e.target);
        }
    });
});

/* =========================================
   API 통신 함수들
   ========================================= */

// 1. 목록 조회
function loadFridgeData() {
    fetch('/fridge/data')
        .then(res => res.json())
        .then(data => {
            currentIngredients = data;
            renderAll(data);
        })
        .catch(err => console.error("데이터 로드 실패:", err));
}

// 2. 재료 추가
function addIngredient(categoryKey) {
    const formData = new FormData();
    formData.append("ingredient", "새 재료");
    formData.append("category", CATEGORY_MAP[categoryKey]);

    fetch('/fridge/registAjax', { method: 'POST', body: formData })
    .then(res => res.json())
    .then(data => {
        if (data.status === "OK") {
            loadFridgeData();
        } else {
            showAutoModal(data.message); 
        }
    })
    .catch(err => console.error("추가 실패:", err));
}

// 3. 재료 수정
function modifyIngredient(id, newName, newCategory, newDate) {
    const formData = new FormData();
    formData.append("id", id);
    formData.append("ingredient", newName);
    formData.append("category", newCategory);
    if(newDate) {
        formData.append("expirationDate", newDate);
    }

    fetch('/fridge/modifyAjax', { method: 'POST', body: formData })
    .then(res => res.json())
    .then(data => {
        if (data.status === "OK") {
            loadFridgeData();
        } else {
            showAutoModal(data.message); 
            loadFridgeData();
        }
    })
    .catch(err => console.error("수정 실패:", err));
}

// 4. 선택 삭제
async function handleDeleteSelected() {
    const checkedBoxes = document.querySelectorAll(".ingredient-check:checked");

    // 선택된 게 없을 때
    if (checkedBoxes.length === 0) {
        showAutoModal("삭제할 재료를 선택해주세요.");
        return;
    }

    // ★ 모달로 확인 받기
    const isConfirmed = await showConfirmModal("선택한 재료를 삭제하시겠습니까?");
    if (!isConfirmed) return; // '아니오' 누르면 중단

    // '예' 눌렀을 때 삭제 진행
    const formData = new FormData();
    checkedBoxes.forEach(chk => {
        const row = chk.closest(".ingredient-row");
        // 중요: 체크박스의 value가 아닌, div의 data-id를 사용
        formData.append("ids[]", row.dataset.id);
    });

    fetch('/fridge/removeMultipleAjax', { method: 'POST', body: formData })
    .then(res => res.json())
    .then(data => {
        if (data.status === "OK") {
            loadFridgeData();
        } else {
            showAutoModal(data.message);
        }
    })
    .catch(err => console.error("삭제 실패:", err));
}


/* =========================================
   렌더링 관련
   ========================================= */

function renderAll(dataList) {
    const freezerList = document.getElementById("freezerList");
    const fridgeList = document.getElementById("fridgeList");
    const roomList = document.getElementById("roomList");

    freezerList.innerHTML = "";
    fridgeList.innerHTML = "";
    roomList.innerHTML = "";

    dataList.forEach(item => {
        const categoryKey = REVERSE_MAP[item.category];
        let container = null;

        if (categoryKey === "freezer") container = freezerList;
        else if (categoryKey === "fridge") container = fridgeList;
        else if (categoryKey === "room") container = roomList;

        if (container) {
            container.insertAdjacentHTML("beforeend", createItemHTML(item));
        }
    });
}

function createItemHTML(item) {
    const ddayInfo = getDDayInfo(item.expirationDate);
    const dateText = item.expirationDate || "유통기한 등록";

    // data-id: 재료 ID, ingredient-check: 선택용 체크박스
    return `
        <div class="ingredient-row" data-id="${item.id}" data-category="${item.category}">
            <label class="check-wrap">
                <input type="checkbox" class="ingredient-check" name="ingredientId" value="${item.id}" /> 
            </label>

            <div class="ingredient-info">
                <div class="top-line">
                    <span class="ingredient-name" data-id="${item.id}">${item.ingredient}</span>
                    <span class="ingredient-date" data-id="${item.id}">${dateText}</span>
                    ${
                        item.expirationDate
                        ? `<span class="ingredient-dday ${ddayInfo.className}">${ddayInfo.label}</span>`
                        : ""
                    }
                </div>
                <div class="underline"></div>
            </div>
        </div>
    `;
}

function getDDayInfo(dateStr) {
    if (!dateStr) return { label: "", className: "" };

    const today = new Date();
    today.setHours(0, 0, 0, 0); // 오늘 자정
    const target = new Date(dateStr);
    target.setHours(0, 0, 0, 0); // 대상 날짜 자정

    const diffTime = target - today;
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24)); 

    if (diffDays < 0) return { label: "유통기한 지남", className: "expired" };
    if (diffDays === 0) return { label: "D-day", className: "warning" };
    if (diffDays <= 3) return { label: `D-${diffDays}`, className: "warning" };
    return { label: `D-${diffDays}`, className: "" };
}

/* =========================================
   인라인 수정 로직
   ========================================= */

// 이름 수정
function enterNameEditMode(nameEl) {
    const id = Number(nameEl.dataset.id);
    const itemData = currentIngredients.find(i => i.id === id);
    if(!itemData) return;

    const originalName = nameEl.textContent;
    const input = document.createElement("input");
    input.type = "text";
    input.value = originalName;
    input.className = "edit-inline"; 
    
    input.style.width = "100px";
    input.style.padding = "2px 5px";
    input.style.border = "1px solid #6bbd45";
    input.style.borderRadius = "4px";

    nameEl.replaceWith(input);
    input.focus();

    const save = () => {
        const newName = input.value.trim();
        if (newName && newName !== originalName) {
            modifyIngredient(id, newName, itemData.category, itemData.expirationDate);
        } else {
            input.replaceWith(nameEl);
        }
    };

    input.addEventListener("blur", save);
    input.addEventListener("keypress", (e) => {
        if (e.key === "Enter") input.blur();
    });
}

// 날짜 수정
function enterDateEditMode(dateEl) {
    const id = Number(dateEl.dataset.id);
    const itemData = currentIngredients.find(i => i.id === id);
    if(!itemData) return;

    const originalDate = itemData.expirationDate ? itemData.expirationDate : "";
    const input = document.createElement("input");
    input.type = "date";
    input.value = originalDate;
    input.className = "edit-inline";
    
    input.style.padding = "2px 5px";
    input.style.border = "1px solid #6bbd45";
    input.style.borderRadius = "4px";
    input.style.fontSize = "12px";
    
    dateEl.replaceWith(input);
    input.focus();

    const save = () => {
        const newDate = input.value;
        if (newDate !== originalDate) {
            modifyIngredient(id, itemData.ingredient, itemData.category, newDate);
        } else {
            input.replaceWith(dateEl);
        }
    };

    input.addEventListener("blur", save);
    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") input.blur();
    });
}

/* =========================================
   레시피 페이지 이동 (AI 추천 로직)
   ========================================= */

/**
 * ⭐️ 현재 냉장고 목록에서 체크된 모든 재료의 ID를 수집합니다.
 */
function getSelectedIngredientIds() {
    // name="ingredientId"를 가진 모든 체크박스 중에서 체크된 항목을 찾음
    const checkedCheckboxes = document.querySelectorAll('input[type="checkbox"][name="ingredientId"]:checked');
    const ids = [];
    checkedCheckboxes.forEach(checkbox => {
        // 체크박스의 value는 재료 ID입니다.
        ids.push(checkbox.value);
    });
    return ids;
}


/**
 * ⭐️ 레시피 추천 요청을 처리하고, 결과를 받아 현재 창을 상세 페이지로 이동합니다.
 * @param {string} mode 'selected' 또는 'all'
 */
function handleRecipeRecommendation(mode) {
    const selectedIds = getSelectedIngredientIds();
    let idsString = '';

    if (mode === 'selected') {
        if (selectedIds.length === 0) {
            showAutoModal("선택 재료로 레시피를 불러오려면 재료를 하나 이상 선택해주세요.");
            return;
        }
        idsString = selectedIds.join(',');
    } else if (mode === 'all') {
        // 전체 재료로 요청할 때는 idsString을 빈 문자열로 남겨서 Controller가 전체 재료 로직을 타게 함
        // (Controller에서 selectedIds=""이면 전체 재료로 처리하고 있습니다.)
        // 다만, 전체 재료가 없는 경우를 위한 추가 확인 로직을 넣을 수 있음
        if (currentIngredients.length === 0) {
            showAutoModal("냉장고에 등록된 재료가 없어 AI 추천을 요청할 수 없습니다.");
            return;
        }
        idsString = ''; // Controller의 로직에 따라 빈 문자열 또는 모든 재료 ID를 사용 가능. 현재는 Controller가 빈 문자열이면 전체 재료를 처리함
    } else {
        return;
    }

    // [💡 로딩 시작]
    showLoading();

    // 2. 서버의 /fridge/recommend API 호출 (POST AJAX)
    fetch('/fridge/recommend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        // selectedIds 파라미터로 선택된 ID 목록 (또는 전체 재료 요청 시 빈 문자열)을 전송
        body: `selectedIds=${idsString}`
    })
    .then(response => response.json())
    .then(data => {
        // [💡 로딩 종료]
        hideLoading();

        if (data.status === 'OK' && data.recipeId) {
            // 3. 성공적으로 레시피 ID를 받은 경우, 현재 창 이동
            const recipeId = data.recipeId;
            const aiMessage = encodeURIComponent(data.aiMessage); // 메시지에 특수문자가 있을 수 있으므로 인코딩
            const recipeQuery = encodeURIComponent(data.recipeQuery); // 재추천용 쿼리도 인코딩

            // URL 구성: /fridge/recipe-detail?recipeId=...
            const detailUrl = `/fridge/recipe-detail?recipeId=${recipeId}&aiMessage=${aiMessage}&recipeQuery=${recipeQuery}`;

            // ⭐️ 창 이동 (현재 페이지에서 상세 페이지로 이동)
            window.location.href = detailUrl;

        } else if (data.status === 'OK' && !data.recipeId) {
            // AI가 레시피는 찾지 못했으나 응답은 준 경우
            showAutoModal("AI가 적절한 레시피를 찾지 못했습니다. AI 답변: " + data.aiMessage);
        } else if (data.status === 'UNAUTHORIZED') {
            showAutoModal("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = "/user/loginForm";
        } else {
            showAutoModal("레시피 추천 중 오류가 발생했습니다: " + (data.message || "서버 오류"));
        }
    })
    .catch(error => {
        // [💡 로딩 종료]
        hideLoading();
        console.error('레시피 추천 API 호출 오류:', error);
        showAutoModal('서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
    });
}


// ----------------------------------------------------
// 💡 로딩 함수는 실제 프로젝트에 맞게 구현 필요
function showLoading() {
    // 예: 화면 중앙에 스피너 표시 로직
    console.log("로딩 시작...");
    // 실제 구현 시, 사용자에게 로딩 중임을 명확히 보여줘야 함
}
function hideLoading() {
    // 예: 스피너 숨김 로직
    console.log("로딩 종료.");
}
// ----------------------------------------------------