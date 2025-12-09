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

    // 4. 레시피 불러오기 버튼
    document.getElementById("recipeBtn")
        .addEventListener("click", handleRecipeButton);

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
            showAutoModal(data.message); // 모달로 변경
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
            showAutoModal(data.message); // 모달로 변경
            loadFridgeData();
        }
    })
    .catch(err => console.error("수정 실패:", err));
}

// 4. 선택 삭제 (여기가 핵심 수정 부분!)
async function handleDeleteSelected() {
    const checkedBoxes = document.querySelectorAll(".ingredient-check:checked");
    
    // 선택된 게 없을 때
    if (checkedBoxes.length === 0) {
        showAutoModal("삭제할 재료를 선택해주세요.");
        return;
    }

    // ★ 모달로 확인 받기 (await 사용)
    const isConfirmed = await showConfirmModal("선택한 재료를 삭제하시겠습니까?");
    if (!isConfirmed) return; // '아니오' 누르면 중단

    // '예' 눌렀을 때 삭제 진행
    const formData = new FormData();
    checkedBoxes.forEach(chk => {
        const row = chk.closest(".ingredient-row");
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

    return `
        <div class="ingredient-row" data-id="${item.id}" data-category="${item.category}">
            <label class="check-wrap">
                <input type="checkbox" class="ingredient-check" />
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
   레시피 페이지 이동
   ========================================= */
function handleRecipeButton() {
    if (currentIngredients.length === 0) {
        showAutoModal("냉장고에 등록된 재료가 없습니다"); // 모달로 변경
        return;
    }

    // TODO: 레시피 페이지 이동 로직 구현
    showAutoModal("레시피 검색 기능 구현 예정입니다.\n현재 등록된 재료 수: " + currentIngredients.length);
}