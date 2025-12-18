/* [카테고리 매핑] */
const CATEGORY_MAP = { freezer: "냉동", fridge: "냉장", room: "상온" };
const REVERSE_MAP = { "냉동": "freezer", "냉장": "fridge", "상온": "room" };

let currentIngredients = [];

document.addEventListener("DOMContentLoaded", () => {
    loadFridgeData();

    document.querySelectorAll(".add-ingredient-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const categoryKey = btn.dataset.category;
            addIngredient(categoryKey);
        });
    });

    document.getElementById("deleteSelectedBtn").addEventListener("click", handleDeleteSelected);

    // 전체 재료로 레시피 불러오기 버튼 연결
    const recipeAllBtn = document.getElementById('recipeAllBtn');
    if (recipeAllBtn) {
        recipeAllBtn.addEventListener('click', () => handleRecipeRecommendation());
    }

    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("ingredient-name")) enterNameEditMode(e.target);
        if (e.target.classList.contains("ingredient-date")) enterDateEditMode(e.target);
    });
});

/* API 통신 */
function loadFridgeData() {
    fetch('/fridge/data')
        .then(res => res.json())
        .then(data => {
            currentIngredients = data;
            renderAll(data);
        }).catch(err => console.error(err));
}

function addIngredient(categoryKey) {
    const formData = new FormData();
    formData.append("ingredient", "새 재료");
    formData.append("category", CATEGORY_MAP[categoryKey]);
    fetch('/fridge/registAjax', { method: 'POST', body: formData })
    .then(res => res.json()).then(data => {
        if (data.status === "OK") loadFridgeData();
        else showAutoModal(data.message);
    });
}

function modifyIngredient(id, newName, newCategory, newDate) {
    const formData = new FormData();
    formData.append("id", id);
    formData.append("ingredient", newName);
    formData.append("category", newCategory);
    if(newDate) formData.append("expirationDate", newDate);

    fetch('/fridge/modifyAjax', { method: 'POST', body: formData })
    .then(res => res.json()).then(data => {
        if (data.status === "OK") loadFridgeData();
        else { showAutoModal(data.message); loadFridgeData(); }
    });
}

async function handleDeleteSelected() {
    const checkedBoxes = document.querySelectorAll(".ingredient-check:checked");
    if (checkedBoxes.length === 0) {
        showAutoModal("삭제할 재료를 선택해주세요."); return;
    }
    const isConfirmed = await showConfirmModal("선택한 재료를 삭제하시겠습니까?");
    if (!isConfirmed) return;

    const formData = new FormData();
    checkedBoxes.forEach(chk => {
        const row = chk.closest(".ingredient-row");
        formData.append("ids[]", row.dataset.id);
    });

    fetch('/fridge/removeMultipleAjax', { method: 'POST', body: formData })
    .then(res => res.json()).then(data => {
        if (data.status === "OK") loadFridgeData();
        else showAutoModal(data.message);
    });
}

/* 렌더링 함수들 (생략 없음) */
function renderAll(dataList) {
    const freezerList = document.getElementById("freezerList");
    const fridgeList = document.getElementById("fridgeList");
    const roomList = document.getElementById("roomList");
    freezerList.innerHTML = ""; fridgeList.innerHTML = ""; roomList.innerHTML = "";

    dataList.forEach(item => {
        const categoryKey = REVERSE_MAP[item.category];
        let container = (categoryKey === "freezer") ? freezerList : (categoryKey === "fridge") ? fridgeList : (categoryKey === "room") ? roomList : null;
        if (container) container.insertAdjacentHTML("beforeend", createItemHTML(item));
    });
}

function createItemHTML(item) {
    const ddayInfo = getDDayInfo(item.expirationDate);
    const dateText = item.expirationDate || "유통기한 등록";
    return `
        <div class="ingredient-row" data-id="${item.id}">
            <label class="check-wrap">
                <input type="checkbox" class="ingredient-check" name="ingredientId" value="${item.id}" /> 
            </label>
            <div class="ingredient-info">
                <div class="top-line">
                    <span class="ingredient-name" data-id="${item.id}">${item.ingredient}</span>
                    <span class="ingredient-date" data-id="${item.id}">${dateText}</span>
                    ${item.expirationDate ? `<span class="ingredient-dday ${ddayInfo.className}">${ddayInfo.label}</span>` : ""}
                </div>
                <div class="underline"></div>
            </div>
        </div>
    `;
}

function getDDayInfo(dateStr) {
    if (!dateStr) return { label: "", className: "" };
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const target = new Date(dateStr); target.setHours(0, 0, 0, 0);
    const diffDays = Math.round((target - today) / (1000 * 60 * 60 * 24));
    if (diffDays < 0) return { label: "유통기한 지남", className: "expired" };
    if (diffDays <= 3) return { label: (diffDays === 0) ? "D-day" : `D-${diffDays}`, className: "warning" };
    return { label: `D-${diffDays}`, className: "" };
}

/* 인라인 수정 */
function enterNameEditMode(nameEl) {
    const id = Number(nameEl.dataset.id);
    const itemData = currentIngredients.find(i => i.id === id);
    const originalName = nameEl.textContent;
    const input = document.createElement("input");
    input.type = "text"; input.value = originalName; input.className = "edit-inline";
    nameEl.replaceWith(input); input.focus();
    const save = () => {
        const newName = input.value.trim();
        if (newName && newName !== originalName) modifyIngredient(id, newName, itemData.category, itemData.expirationDate);
        else input.replaceWith(nameEl);
    };
    input.onblur = save;
    input.onkeypress = (e) => { if (e.key === "Enter") input.blur(); };
}

function enterDateEditMode(dateEl) {
    const id = Number(dateEl.dataset.id);
    const itemData = currentIngredients.find(i => i.id === id);
    const originalDate = itemData.expirationDate || "";
    const input = document.createElement("input");
    input.type = "date"; input.value = originalDate;
    dateEl.replaceWith(input); input.focus();
    const save = () => {
        const newDate = input.value;
        if (newDate !== originalDate) modifyIngredient(id, itemData.ingredient, itemData.category, newDate);
        else input.replaceWith(dateEl);
    };
    input.onblur = save;
}

/* =========================================
   핵심 기능: 레시피 추천 및 이동
   ========================================= */
function handleRecipeRecommendation() {
    const btn = document.getElementById('recipeAllBtn');
    if (currentIngredients.length === 0) {
        showAutoModal("냉장고에 등록된 재료가 없습니다."); return;
    }

    // 1. 버튼 상태 로딩으로 변경
    btn.classList.add('loading');
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> 야미가 레시피를 추천 중입니다...`;

    // 2. API 호출
    fetch('/fridge/recommend', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `selectedIds=` // 전체 재료 모드
    })
    .then(res => res.json())
    .then(data => {
        if (data.status === 'OK' && data.recipeId) {
            // 3. 불러오기 성공: 버튼을 '보러가기' 상태로 전환
            btn.classList.remove('loading');
            btn.classList.add('ready');
            btn.disabled = false;
            btn.innerHTML = `<i class="fa-solid fa-utensils"></i> 추천 레시피 보러가기 →`;
            
            // 4. 클릭 시 이동 이벤트로 교체 (targetId 파라미터 전달)
            btn.onclick = () => {
                window.location.href = `/recipe/list?targetId=${data.recipeId}`;
            };
        } else {
            btn.classList.remove('loading');
            btn.disabled = false;
            btn.innerHTML = `전체 재료로 레시피 불러오기`;
            showAutoModal(data.message || "레시피를 찾지 못했습니다.");
        }
    })
    .catch(err => {
        console.error(err);
        btn.classList.remove('loading');
        btn.disabled = false;
        btn.innerHTML = `전체 재료로 레시피 불러오기`;
    });
}